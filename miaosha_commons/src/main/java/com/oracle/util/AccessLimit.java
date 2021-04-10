package com.oracle.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.METHOD) // 表示当前注解放在方法上
@Retention(RetentionPolicy.RUNTIME) //表示当前注解的存活时间是运行时
public @interface AccessLimit {
    int seconds(); // 时间范围
    int maxCount(); // 次数
    boolean needLogin() default true; // 是否需要登陆，默认需要登录

}
