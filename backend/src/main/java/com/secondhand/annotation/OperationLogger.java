package com.secondhand.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLogger {
    
    /**
     * 操作描述
     */
    String value() default "";
}
