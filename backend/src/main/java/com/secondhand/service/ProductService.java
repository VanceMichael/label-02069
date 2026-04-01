package com.secondhand.service;

import com.secondhand.common.PageResult;
import com.secondhand.dto.ProductPublishDTO;
import com.secondhand.dto.ProductQueryDTO;
import com.secondhand.vo.ProductVO;

/**
 * 商品服务接口
 */
public interface ProductService {
    
    /**
     * 发布商品
     */
    ProductVO publish(ProductPublishDTO dto);
    
    /**
     * 商品列表
     */
    PageResult<ProductVO> list(ProductQueryDTO dto);
    
    /**
     * 商品详情
     */
    ProductVO getById(Long id);
    
    /**
     * 我发布的商品
     */
    PageResult<ProductVO> myProducts(ProductQueryDTO dto);
    
    /**
     * 更新商品
     */
    void update(Long id, ProductPublishDTO dto);
    
    /**
     * 下架商品
     */
    void offline(Long id);
    
    /**
     * 验证商品链上数据
     */
    boolean verifyBlockchain(Long id);
}
