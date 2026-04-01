package com.secondhand.controller;

import com.secondhand.annotation.OperationLogger;
import com.secondhand.common.Result;
import com.secondhand.dto.UserLoginDTO;
import com.secondhand.dto.UserRegisterDTO;
import com.secondhand.dto.UserUpdateDTO;
import com.secondhand.service.UserService;
import com.secondhand.vo.LoginVO;
import com.secondhand.vo.UserVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    @OperationLogger("用户注册")
    public Result<Void> register(@Valid @RequestBody UserRegisterDTO dto) {
        userService.register(dto);
        return Result.success();
    }
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    @OperationLogger("用户登录")
    public Result<LoginVO> login(@Valid @RequestBody UserLoginDTO dto) {
        return Result.success(userService.login(dto));
    }
    
    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public Result<UserVO> info() {
        return Result.success(userService.getCurrentUser());
    }
    
    /**
     * 更新用户信息
     */
    @PutMapping("/update")
    @OperationLogger("更新用户信息")
    public Result<Void> update(@RequestBody UserUpdateDTO dto) {
        userService.updateUser(dto);
        return Result.success();
    }
}
