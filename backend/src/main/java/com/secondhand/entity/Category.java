package com.secondhand.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 商品分类实体
 */
@Data
@TableName("category")
public class Category {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name;
    
    private String icon;
    
    private Integer sort;
    
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
