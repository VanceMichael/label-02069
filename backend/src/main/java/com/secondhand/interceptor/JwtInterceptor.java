package com.secondhand.interceptor;

import com.secondhand.common.BusinessException;
import com.secondhand.common.Constants;
import com.secondhand.util.JwtUtil;
import com.secondhand.util.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT 拦截器
 */
@Slf4j
@Component
public class JwtInterceptor implements HandlerInterceptor {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // OPTIONS 请求直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        
        // 商品详情页允许未登录访问（GET /api/product/detail/数字ID）
        String uri = request.getRequestURI();
        if ("GET".equalsIgnoreCase(request.getMethod()) && uri.matches("/api/product/detail/\\d+")) {
            // 如果有token就解析，没有也放行
            String token = request.getHeader(Constants.JWT_HEADER);
            if (StringUtils.hasText(token)) {
                try {
                    if (token.startsWith(Constants.JWT_PREFIX)) {
                        token = token.substring(Constants.JWT_PREFIX.length());
                    }
                    Long userId = jwtUtil.getUserIdFromToken(token);
                    String username = jwtUtil.getUsernameFromToken(token);
                    String role = jwtUtil.getRoleFromToken(token);
                    UserContext.setUserId(userId);
                    UserContext.setUsername(username);
                    UserContext.setRole(role);
                } catch (Exception ignored) {
                }
            }
            return true;
        }
        
        String token = request.getHeader(Constants.JWT_HEADER);
        if (!StringUtils.hasText(token)) {
            throw new BusinessException(401, "未登录");
        }
        
        if (token.startsWith(Constants.JWT_PREFIX)) {
            token = token.substring(Constants.JWT_PREFIX.length());
        }
        
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            String username = jwtUtil.getUsernameFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);
            
            if (userId == null || !jwtUtil.validateToken(token)) {
                throw new BusinessException(401, "登录已过期");
            }
            
            // 存入上下文
            UserContext.setUserId(userId);
            UserContext.setUsername(username);
            UserContext.setRole(role);
            
            // 管理端接口权限校验
            uri = request.getRequestURI();
            if (uri.startsWith("/api/admin") && !"ADMIN".equals(role)) {
                throw new BusinessException(403, "无权访问管理接口");
            }
            
            return true;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Token解析失败", e);
            throw new BusinessException(401, "无效的Token");
        }
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }
}
