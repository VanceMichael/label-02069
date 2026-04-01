package com.secondhand.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品VO
 */
@Data
public class ProductVO {
    
    private Long id;
    
    private Long sellerId;
    
    private String sellerName;
    
    private String sellerAvatar;
    
    private String title;
    
    private String description;
    
    private BigDecimal price;
    
    private BigDecimal originalPrice;
    
    private String category;
    
    private String images;
    
    private Integer conditionLevel;
    
    private Integer status;
    
    private String statusText;
    
    private Integer viewCount;
    
    private String blockchainHash;
    
    private LocalDateTime blockchainTime;
    
    private LocalDateTime createTime;
}
