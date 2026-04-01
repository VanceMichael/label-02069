package com.secondhand.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.secondhand.common.BusinessException;
import com.secondhand.common.Constants;
import com.secondhand.common.PageResult;
import com.secondhand.dto.OrderCreateDTO;
import com.secondhand.dto.OrderQueryDTO;
import com.secondhand.entity.Order;
import com.secondhand.entity.Product;
import com.secondhand.entity.User;
import com.secondhand.mapper.OrderMapper;
import com.secondhand.mapper.ProductMapper;
import com.secondhand.mapper.UserMapper;
import com.secondhand.service.BlockchainService;
import com.secondhand.service.OrderService;
import com.secondhand.util.UserContext;
import com.secondhand.vo.OrderVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    
    private final OrderMapper orderMapper;
    private final ProductMapper productMapper;
    private final UserMapper userMapper;
    private final BlockchainService blockchainService;
    
    @Override
    @Transactional
    public OrderVO create(OrderCreateDTO dto) {
        Long userId = UserContext.getUserId();
        
        // 查询商品
        Product product = productMapper.selectById(dto.getProductId());
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        if (product.getStatus() != Constants.PRODUCT_STATUS_ON) {
            throw new BusinessException("商品已下架或已售出");
        }
        if (product.getSellerId().equals(userId)) {
            throw new BusinessException("不能购买自己的商品");
        }
        
        // 创建订单
        Order order = new Order();
        order.setOrderNo(IdUtil.getSnowflakeNextIdStr());
        order.setBuyerId(userId);
        order.setSellerId(product.getSellerId());
        order.setProductId(product.getId());
        order.setProductTitle(product.getTitle());
        order.setProductImage(product.getImages() != null ? product.getImages().split(",")[0] : null);
        order.setAmount(product.getPrice());
        order.setStatus(Constants.ORDER_STATUS_PENDING);
        order.setRemark(dto.getRemark());
        
        orderMapper.insert(order);
        log.info("订单创建成功: orderNo={}, buyerId={}", order.getOrderNo(), userId);
        
        return toOrderVO(order);
    }
    
    @Override
    public PageResult<OrderVO> list(OrderQueryDTO dto) {
        Long userId = UserContext.getUserId();
        Page<Order> page = new Page<>(dto.getCurrent(), dto.getSize());
        
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        
        // 根据类型筛选
        if ("buy".equals(dto.getType())) {
            wrapper.eq(Order::getBuyerId, userId);
        } else if ("sell".equals(dto.getType())) {
            wrapper.eq(Order::getSellerId, userId);
        } else {
            wrapper.and(w -> w.eq(Order::getBuyerId, userId).or().eq(Order::getSellerId, userId));
        }
        
        if (dto.getStatus() != null) {
            wrapper.eq(Order::getStatus, dto.getStatus());
        }
        
        wrapper.orderByDesc(Order::getCreateTime);
        
        Page<Order> result = orderMapper.selectPage(page, wrapper);
        
        List<OrderVO> records = result.getRecords().stream()
                .map(this::toOrderVO)
                .collect(Collectors.toList());
        
        return PageResult.of(records, result.getTotal(), result.getCurrent(), result.getSize());
    }
    
    @Override
    public OrderVO getById(Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        return toOrderVO(order);
    }
    
    @Override
    @Transactional
    public void pay(Long id) {
        Long userId = UserContext.getUserId();
        Order order = orderMapper.selectById(id);
        
        validateOrder(order, userId, true);
        
        if (order.getStatus() != Constants.ORDER_STATUS_PENDING) {
            throw new BusinessException("订单状态不正确");
        }
        
        // 检查余额
        User buyer = userMapper.selectById(userId);
        if (buyer.getBalance().compareTo(order.getAmount()) < 0) {
            throw new BusinessException("余额不足");
        }
        
        // 扣除买家余额
        buyer.setBalance(buyer.getBalance().subtract(order.getAmount()));
        userMapper.updateById(buyer);
        
        // 更新订单状态
        order.setStatus(Constants.ORDER_STATUS_PAID);
        order.setPayTime(LocalDateTime.now());
        orderMapper.updateById(order);
        
        // 更新商品状态
        Product product = productMapper.selectById(order.getProductId());
        product.setStatus(Constants.PRODUCT_STATUS_SOLD);
        productMapper.updateById(product);
        
        log.info("订单支付成功: orderNo={}", order.getOrderNo());
    }
    
    @Override
    public void ship(Long id) {
        Long userId = UserContext.getUserId();
        Order order = orderMapper.selectById(id);
        
        validateOrder(order, userId, false);
        
        if (order.getStatus() != Constants.ORDER_STATUS_PAID) {
            throw new BusinessException("订单状态不正确");
        }
        
        order.setStatus(Constants.ORDER_STATUS_SHIPPED);
        order.setShipTime(LocalDateTime.now());
        orderMapper.updateById(order);
        
        log.info("订单发货: orderNo={}", order.getOrderNo());
    }
    
    @Override
    @Transactional
    public void confirm(Long id) {
        Long userId = UserContext.getUserId();
        Order order = orderMapper.selectById(id);
        
        validateOrder(order, userId, true);
        
        if (order.getStatus() != Constants.ORDER_STATUS_SHIPPED) {
            throw new BusinessException("订单状态不正确");
        }
        
        // 给卖家加余额
        User seller = userMapper.selectById(order.getSellerId());
        seller.setBalance(seller.getBalance().add(order.getAmount()));
        userMapper.updateById(seller);
        
        // 更新订单状态
        order.setStatus(Constants.ORDER_STATUS_COMPLETED);
        order.setCompleteTime(LocalDateTime.now());
        orderMapper.updateById(order);
        
        log.info("订单确认收货: orderNo={}", order.getOrderNo());
        
        // 交易上链（recordTrade 内部已有完整 try-catch，不会抛异常）
        try {
            String txHash = blockchainService.recordTrade(order);
            order.setBlockchainHash(txHash);
            order.setBlockchainTime(LocalDateTime.now());
            orderMapper.updateById(order);
            log.info("交易上链成功: orderNo={}, txHash={}", order.getOrderNo(), txHash);
        } catch (Exception e) {
            log.error("交易上链失败，不影响订单: orderNo={}", order.getOrderNo(), e);
        }
    }
    
    @Override
    @Transactional
    public void cancel(Long id) {
        Long userId = UserContext.getUserId();
        Order order = orderMapper.selectById(id);
        
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getBuyerId().equals(userId) && !order.getSellerId().equals(userId)) {
            throw new BusinessException("无权操作");
        }
        if (order.getStatus() != Constants.ORDER_STATUS_PENDING) {
            throw new BusinessException("只能取消待支付订单");
        }
        
        order.setStatus(Constants.ORDER_STATUS_CANCELLED);
        orderMapper.updateById(order);
        
        log.info("订单取消: orderNo={}", order.getOrderNo());
    }
    
    @Override
    public boolean verifyBlockchain(Long id) {
        return blockchainService.verifyTrade(id);
    }
    
    private void validateOrder(Order order, Long userId, boolean isBuyer) {
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (isBuyer && !order.getBuyerId().equals(userId)) {
            throw new BusinessException("无权操作");
        }
        if (!isBuyer && !order.getSellerId().equals(userId)) {
            throw new BusinessException("无权操作");
        }
    }
    
    private OrderVO toOrderVO(Order order) {
        OrderVO vo = new OrderVO();
        BeanUtil.copyProperties(order, vo);
        
        // 设置状态文本
        vo.setStatusText(getStatusText(order.getStatus()));
        
        // 获取买家卖家信息
        User buyer = userMapper.selectById(order.getBuyerId());
        User seller = userMapper.selectById(order.getSellerId());
        if (buyer != null) {
            vo.setBuyerName(buyer.getNickname());
        }
        if (seller != null) {
            vo.setSellerName(seller.getNickname());
        }
        
        return vo;
    }
    
    private String getStatusText(Integer status) {
        return switch (status) {
            case 0 -> "待支付";
            case 1 -> "已支付";
            case 2 -> "已发货";
            case 3 -> "已完成";
            case 4 -> "已取消";
            default -> "未知";
        };
    }
}
