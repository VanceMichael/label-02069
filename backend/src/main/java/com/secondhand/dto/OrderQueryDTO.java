package com.secondhand.dto;

import lombok.Data;

/**
 * 订单查询DTO
 */
@Data
public class OrderQueryDTO {
    
    private Integer status;
    
    private String type; // buy-我买的, sell-我卖的
    
    private Integer current = 1;
    
    private Integer size = 10;
}
