package com.secondhand.aspect;

import cn.hutool.json.JSONUtil;
import com.secondhand.annotation.OperationLogger;
import com.secondhand.entity.OperationLog;
import com.secondhand.mapper.OperationLogMapper;
import com.secondhand.util.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 操作日志切面
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {
    
    private final OperationLogMapper operationLogMapper;
    
    @Around("@annotation(operationLogger)")
    public Object around(ProceedingJoinPoint point, OperationLogger operationLogger) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        OperationLog operationLog = new OperationLog();
        operationLog.setOperation(operationLogger.value());
        operationLog.setUserId(UserContext.getUserId());
        operationLog.setUsername(UserContext.getUsername());
        
        // 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            operationLog.setMethod(request.getMethod() + " " + request.getRequestURI());
            operationLog.setIp(getClientIp(request));
        }
        
        // 获取参数
        MethodSignature signature = (MethodSignature) point.getSignature();
        String[] paramNames = signature.getParameterNames();
        Object[] args = point.getArgs();
        if (paramNames != null && args != null) {
            StringBuilder params = new StringBuilder();
            for (int i = 0; i < paramNames.length; i++) {
                if (i > 0) params.append(", ");
                params.append(paramNames[i]).append("=");
                try {
                    params.append(JSONUtil.toJsonStr(args[i]));
                } catch (Exception e) {
                    params.append(args[i]);
                }
            }
            operationLog.setParams(params.toString());
        }
        
        Object result;
        try {
            result = point.proceed();
            operationLog.setResult(1);
            
            // 特殊处理：登录成功后从返回结果中获取用户名
            if ("\u7528\u6237\u767b\u5f55".equals(operationLogger.value()) && operationLog.getUsername() == null) {
                try {
                    if (result != null) {
                        // 通过反射获取 Result.data.user.username
                        Object data = result.getClass().getMethod("getData").invoke(result);
                        if (data != null) {
                            Object user = data.getClass().getMethod("getUser").invoke(data);
                            if (user != null) {
                                Object username = user.getClass().getMethod("getUsername").invoke(user);
                                if (username != null) {
                                    operationLog.setUsername(username.toString());
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    log.debug("\u83b7\u53d6\u767b\u5f55\u7528\u6237\u540d\u5931\u8d25", e);
                }
            }
        } catch (Throwable e) {
            operationLog.setResult(0);
            operationLog.setErrorMsg(e.getMessage());
            throw e;
        } finally {
            operationLog.setCostTime(System.currentTimeMillis() - startTime);
            
            try {
                operationLogMapper.insert(operationLog);
            } catch (Exception e) {
                log.error("\u4fdd\u5b58\u64cd\u4f5c\u65e5\u5fd7\u5931\u8d25", e);
            }
        }
        
        return result;
    }
    
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
