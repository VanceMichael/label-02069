package com.secondhand.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 区块链记录VO
 */
@Data
public class BlockchainRecordVO {
    
    private Long id;
    
    private String txHash;
    
    private Long blockNumber;
    
    private String dataType;
    
    private String dataTypeText;
    
    private Long refId;
    
    private String dataContent;
    
    private String dataHash;
    
    private Integer status;
    
    private LocalDateTime createTime;
}
