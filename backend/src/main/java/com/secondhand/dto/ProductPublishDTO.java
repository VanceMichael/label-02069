package com.secondhand.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 商品发布DTO
 */
@Data
public class ProductPublishDTO {
    
    @NotBlank(message = "商品标题不能为空")
    @Size(max = 100, message = "标题最多100字")
    private String title;
    
    private String description;
    
    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.01", message = "价格必须大于0")
    private BigDecimal price;
    
    private BigDecimal originalPrice;
    
    private String category;
    
    private String images;
    
    @Min(value = 1, message = "成色1-10")
    @Max(value = 10, message = "成色1-10")
    private Integer conditionLevel = 9;
}
