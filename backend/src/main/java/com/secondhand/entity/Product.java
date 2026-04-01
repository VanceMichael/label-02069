package com.secondhand.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体
 */
@Data
@TableName("product")
public class Product {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long sellerId;
    
    private String title;
    
    private String description;
    
    private BigDecimal price;
    
    private BigDecimal originalPrice;
    
    private Long categoryId;
    
    private String category;
    
    private String images;
    
    private Integer conditionLevel;
    
    private Integer status;
    
    private Integer viewCount;
    
    private String blockchainHash;
    
    private LocalDateTime blockchainTime;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
    
    // 非数据库字段
    @TableField(exist = false)
    private String sellerName;
    
    @TableField(exist = false)
    private String sellerAvatar;
}
