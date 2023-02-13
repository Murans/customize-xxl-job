package com.customize.xxljob.executer.annotation;

import com.customize.xxljob.executer.model.ExecutorRouteStrategyEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自动注册任务 注解
 *
 * @author zhangxu
 * @date 2023/2/13
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoRegisterJob {

    String registerType() default "manual"; //auto 自动注册，manual 手动注册

    String appName();

    String groupDesc();

    String cron();

    ExecutorRouteStrategyEnum route() default ExecutorRouteStrategyEnum.FIRST;

    String author() default "admin";

    String childJob() default ""; //子任务
}
