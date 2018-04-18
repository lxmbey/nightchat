package com.nightchat.common;

import java.lang.annotation.*;
/**
 * 标识请求接口不需要登录的注解
 */
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NotLogin {

}
