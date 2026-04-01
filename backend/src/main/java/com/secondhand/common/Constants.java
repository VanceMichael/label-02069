package com.secondhand.common;

/**
 * 常量定义
 */
public class Constants {
    
    // 用户状态
    public static final int USER_STATUS_DISABLED = 0;
    public static final int USER_STATUS_NORMAL = 1;
    
    // 商品状态
    public static final int PRODUCT_STATUS_OFF = 0;      // 下架
    public static final int PRODUCT_STATUS_ON = 1;       // 在售
    public static final int PRODUCT_STATUS_SOLD = 2;     // 已售
    
    // 订单状态
    public static final int ORDER_STATUS_PENDING = 0;    // 待支付
    public static final int ORDER_STATUS_PAID = 1;       // 已支付
    public static final int ORDER_STATUS_SHIPPED = 2;    // 已发货
    public static final int ORDER_STATUS_COMPLETED = 3;  // 已完成
    public static final int ORDER_STATUS_CANCELLED = 4;  // 已取消
    
    // 区块链数据类型
    public static final String BLOCKCHAIN_TYPE_PRODUCT = "PRODUCT";
    public static final String BLOCKCHAIN_TYPE_TRADE = "TRADE";
    
    // JWT Header
    public static final String JWT_HEADER = "Authorization";
    public static final String JWT_PREFIX = "Bearer ";
}
