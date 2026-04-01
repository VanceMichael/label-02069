package com.secondhand.service;

import com.secondhand.dto.UserLoginDTO;
import com.secondhand.dto.UserRegisterDTO;
import com.secondhand.dto.UserUpdateDTO;
import com.secondhand.vo.LoginVO;
import com.secondhand.vo.UserVO;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 用户注册
     */
    void register(UserRegisterDTO dto);
    
    /**
     * 用户登录
     */
    LoginVO login(UserLoginDTO dto);
    
    /**
     * 获取当前用户信息
     */
    UserVO getCurrentUser();
    
    /**
     * 更新用户信息
     */
    void updateUser(UserUpdateDTO dto);
    
    /**
     * 根据ID获取用户
     */
    UserVO getUserById(Long id);
}
