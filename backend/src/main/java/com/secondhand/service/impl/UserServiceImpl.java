package com.secondhand.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.secondhand.common.BusinessException;
import com.secondhand.dto.UserLoginDTO;
import com.secondhand.dto.UserRegisterDTO;
import com.secondhand.dto.UserUpdateDTO;
import com.secondhand.entity.User;
import com.secondhand.mapper.UserMapper;
import com.secondhand.service.UserService;
import com.secondhand.util.JwtUtil;
import com.secondhand.util.UserContext;
import com.secondhand.vo.LoginVO;
import com.secondhand.vo.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 用户服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    @Override
    public void register(UserRegisterDTO dto) {
        // 检查用户名是否存在
        User existUser = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername())
        );
        if (existUser != null) {
            throw new BusinessException("用户名已存在");
        }
        
        // 创建用户
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setNickname(dto.getNickname() != null ? dto.getNickname() : dto.getUsername());
        user.setPhone(dto.getPhone());
        user.setBalance(new BigDecimal("1000.00")); // 初始余额
        user.setRole("USER");
        user.setStatus(1);
        
        userMapper.insert(user);
        log.info("用户注册成功: {}", dto.getUsername());
    }
    
    @Override
    public LoginVO login(UserLoginDTO dto) {
        // 查询用户
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername())
        );
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }
        
        // 验证密码
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        
        // 检查状态
        if (user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }
        
        // 生成Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        
        // 构建响应
        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setUser(toUserVO(user));
        
        log.info("用户登录成功: {}", dto.getUsername());
        return vo;
    }
    
    @Override
    public UserVO getCurrentUser() {
        Long userId = UserContext.getUserId();
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return toUserVO(user);
    }
    
    @Override
    public void updateUser(UserUpdateDTO dto) {
        Long userId = UserContext.getUserId();
        User user = new User();
        user.setId(userId);
        BeanUtil.copyProperties(dto, user);
        userMapper.updateById(user);
        log.info("用户信息更新: userId={}", userId);
    }
    
    @Override
    public UserVO getUserById(Long id) {
        User user = userMapper.selectById(id);
        return user != null ? toUserVO(user) : null;
    }
    
    private UserVO toUserVO(User user) {
        UserVO vo = new UserVO();
        BeanUtil.copyProperties(user, vo);
        return vo;
    }
}
