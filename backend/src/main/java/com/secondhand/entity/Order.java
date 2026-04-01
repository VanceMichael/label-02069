package com.secondhand.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体
 */
@Data
@TableName("`order`")
public class Order {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String orderNo;
    
    private Long buyerId;
    
    private Long sellerId;
    
    private Long productId;
    
    private String productTitle;
    
    private String productImage;
    
    private BigDecimal amount;
    
    private Integer status;
    
    private LocalDateTime payTime;
    
    private LocalDateTime shipTime;
    
    private LocalDateTime completeTime;
    
    private String blockchainHash;
    
    private LocalDateTime blockchainTime;
    
    private String remark;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
    
    // 非数据库字段
    @TableField(exist = false)
    private String buyerName;
    
    @TableField(exist = false)
    private String sellerName;
}
