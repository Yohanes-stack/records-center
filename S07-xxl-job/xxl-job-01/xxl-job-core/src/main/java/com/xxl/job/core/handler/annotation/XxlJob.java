package com.xxl.job.core.handler.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface XxlJob {

    //定时任务的名称
    String value();

    //初始化方法
    String init() default "";

    //销毁方法
    String destroy() default "";

}