package com.secondhand.service;

import com.secondhand.common.PageResult;
import com.secondhand.dto.OrderCreateDTO;
import com.secondhand.dto.OrderQueryDTO;
import com.secondhand.vo.OrderVO;

/**
 * 订单服务接口
 */
public interface OrderService {
    
    /**
     * 创建订单
     */
    OrderVO create(OrderCreateDTO dto);
    
    /**
     * 订单列表
     */
    PageResult<OrderVO> list(OrderQueryDTO dto);
    
    /**
     * 订单详情
     */
    OrderVO getById(Long id);
    
    /**
     * 支付订单
     */
    void pay(Long id);
    
    /**
     * 发货
     */
    void ship(Long id);
    
    /**
     * 确认收货
     */
    void confirm(Long id);
    
    /**
     * 取消订单
     */
    void cancel(Long id);
    
    /**
     * 验证交易链上数据
     */
    boolean verifyBlockchain(Long id);
}
