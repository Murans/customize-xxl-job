package com.customize.xxljob.executer.annotation;

import com.customize.xxljob.executer.model.ExecutorRouteStrategyEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoRegisterJob {

    String appName();

    String groupDesc();

    String cron();

    ExecutorRouteStrategyEnum route() default ExecutorRouteStrategyEnum.FIRST;

    String author() default "admin";

    String childJob() default ""; //子任务
}
