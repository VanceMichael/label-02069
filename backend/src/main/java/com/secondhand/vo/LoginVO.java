package com.secondhand.vo;

import lombok.Data;

/**
 * 登录响应VO
 */
@Data
public class LoginVO {
    
    private String token;
    
    private UserVO user;
}
