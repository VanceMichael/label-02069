package com.secondhand.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单VO
 */
@Data
public class OrderVO {
    
    private Long id;
    
    private String orderNo;
    
    private Long buyerId;
    
    private String buyerName;
    
    private Long sellerId;
    
    private String sellerName;
    
    private Long productId;
    
    private String productTitle;
    
    private String productImage;
    
    private BigDecimal amount;
    
    private Integer status;
    
    private String statusText;
    
    private LocalDateTime payTime;
    
    private LocalDateTime shipTime;
    
    private LocalDateTime completeTime;
    
    private String blockchainHash;
    
    private LocalDateTime blockchainTime;
    
    private String remark;
    
    private LocalDateTime createTime;
}
