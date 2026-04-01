package com.secondhand.dto;

import lombok.Data;

/**
 * 商品查询DTO
 */
@Data
public class ProductQueryDTO {
    
    private String keyword;
    
    private String category;
    
    private Integer status;
    
    private Integer current = 1;
    
    private Integer size = 10;
}
