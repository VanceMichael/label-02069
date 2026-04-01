package com.secondhand.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 创建订单DTO
 */
@Data
public class OrderCreateDTO {
    
    @NotNull(message = "商品ID不能为空")
    private Long productId;
    
    private String remark;
}
