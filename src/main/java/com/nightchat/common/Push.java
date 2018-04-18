package com.nightchat.common;

import java.lang.annotation.*;
/**
 * 标识推送接口的注解
 */
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Push {

}
