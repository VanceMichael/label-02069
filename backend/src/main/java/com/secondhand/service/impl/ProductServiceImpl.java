package com.secondhand.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.secondhand.common.BusinessException;
import com.secondhand.common.Constants;
import com.secondhand.common.PageResult;
import com.secondhand.dto.ProductPublishDTO;
import com.secondhand.dto.ProductQueryDTO;
import com.secondhand.entity.Product;
import com.secondhand.entity.User;
import com.secondhand.mapper.ProductMapper;
import com.secondhand.mapper.UserMapper;
import com.secondhand.service.BlockchainService;
import com.secondhand.service.ProductService;
import com.secondhand.util.UserContext;
import com.secondhand.vo.ProductVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    
    private final ProductMapper productMapper;
    private final UserMapper userMapper;
    private final BlockchainService blockchainService;
    
    @Override
    public ProductVO publish(ProductPublishDTO dto) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "未登录，请先登录");
        }
        
        // 创建商品
        Product product = new Product();
        BeanUtil.copyProperties(dto, product);
        product.setSellerId(userId);
        product.setStatus(Constants.PRODUCT_STATUS_ON);
        product.setViewCount(0);
        
        productMapper.insert(product);
        log.info("商品发布成功: productId={}, userId={}", product.getId(), userId);
        
        // 商品上链（异步，不影响发布）
        try {
            String txHash = blockchainService.recordProduct(product);
            product.setBlockchainHash(txHash);
            product.setBlockchainTime(LocalDateTime.now());
            productMapper.updateById(product);
            log.info("商品上链成功: productId={}, txHash={}", product.getId(), txHash);
        } catch (Exception e) {
            log.error("商品上链失败，不影响商品发布: productId={}", product.getId(), e);
        }
        
        return toProductVO(product);
    }
    
    @Override
    public PageResult<ProductVO> list(ProductQueryDTO dto) {
        Page<Product> page = new Page<>(dto.getCurrent(), dto.getSize());
        
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, Constants.PRODUCT_STATUS_ON);
        
        if (StrUtil.isNotBlank(dto.getKeyword())) {
            wrapper.like(Product::getTitle, dto.getKeyword());
        }
        if (StrUtil.isNotBlank(dto.getCategory())) {
            wrapper.eq(Product::getCategory, dto.getCategory());
        }
        
        wrapper.orderByDesc(Product::getCreateTime);
        
        Page<Product> result = productMapper.selectPage(page, wrapper);
        
        List<ProductVO> records = result.getRecords().stream()
                .map(this::toProductVO)
                .collect(Collectors.toList());
        
        return PageResult.of(records, result.getTotal(), result.getCurrent(), result.getSize());
    }
    
    @Override
    public ProductVO getById(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        
        // 增加浏览次数
        product.setViewCount(product.getViewCount() + 1);
        productMapper.updateById(product);
        
        return toProductVO(product);
    }
    
    @Override
    public PageResult<ProductVO> myProducts(ProductQueryDTO dto) {
        Long userId = UserContext.getUserId();
        Page<Product> page = new Page<>(dto.getCurrent(), dto.getSize());
        
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getSellerId, userId);
        
        if (dto.getStatus() != null) {
            wrapper.eq(Product::getStatus, dto.getStatus());
        }
        
        wrapper.orderByDesc(Product::getCreateTime);
        
        Page<Product> result = productMapper.selectPage(page, wrapper);
        
        List<ProductVO> records = result.getRecords().stream()
                .map(this::toProductVO)
                .collect(Collectors.toList());
        
        return PageResult.of(records, result.getTotal(), result.getCurrent(), result.getSize());
    }
    
    @Override
    public void update(Long id, ProductPublishDTO dto) {
        Long userId = UserContext.getUserId();
        Product product = productMapper.selectById(id);
        
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        if (!product.getSellerId().equals(userId)) {
            throw new BusinessException("无权操作");
        }
        
        BeanUtil.copyProperties(dto, product);
        productMapper.updateById(product);
        log.info("商品更新: productId={}", id);
    }
    
    @Override
    public void offline(Long id) {
        Long userId = UserContext.getUserId();
        Product product = productMapper.selectById(id);
        
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        if (!product.getSellerId().equals(userId)) {
            throw new BusinessException("无权操作");
        }
        
        product.setStatus(Constants.PRODUCT_STATUS_OFF);
        productMapper.updateById(product);
        log.info("商品下架: productId={}", id);
    }
    
    @Override
    public boolean verifyBlockchain(Long id) {
        return blockchainService.verifyProduct(id);
    }
    
    private ProductVO toProductVO(Product product) {
        ProductVO vo = new ProductVO();
        BeanUtil.copyProperties(product, vo);
        
        // 设置状态文本
        vo.setStatusText(getStatusText(product.getStatus()));
        
        // 获取卖家信息
        User seller = userMapper.selectById(product.getSellerId());
        if (seller != null) {
            vo.setSellerName(seller.getNickname());
            vo.setSellerAvatar(seller.getAvatar());
        }
        
        return vo;
    }
    
    private String getStatusText(Integer status) {
        return switch (status) {
            case 0 -> "已下架";
            case 1 -> "在售";
            case 2 -> "已售出";
            default -> "未知";
        };
    }
}
