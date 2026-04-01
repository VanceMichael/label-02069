package com.secondhand.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 区块链记录实体
 */
@Data
@TableName("blockchain_record")
public class BlockchainRecord {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String txHash;
    
    private Long blockNumber;
    
    private String dataType;
    
    private Long refId;
    
    private String dataContent;
    
    private String dataHash;
    
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
