package com.secondhand.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户VO
 */
@Data
public class UserVO {
    
    private Long id;
    
    private String username;
    
    private String nickname;
    
    private String phone;
    
    private String avatar;
    
    private BigDecimal balance;
    
    private String role;
    
    private Integer status;
    
    private LocalDateTime createTime;
}
