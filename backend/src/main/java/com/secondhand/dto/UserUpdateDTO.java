package com.secondhand.dto;

import lombok.Data;

/**
 * 用户更新DTO
 */
@Data
public class UserUpdateDTO {
    
    private String nickname;
    
    private String phone;
    
    private String avatar;
}
