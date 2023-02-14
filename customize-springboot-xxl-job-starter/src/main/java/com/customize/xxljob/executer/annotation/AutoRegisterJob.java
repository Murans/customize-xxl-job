package com.customize.xxljob.executer.annotation;

import com.customize.xxljob.executer.constant.XxlJobConstant;
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

    String registerType() default "auto"; //auto 自动注册，manual 手动注册

    /**
     * ---------------------- 任务配置 ----------------------
     */
    String jobDesc();

    String executorHandler();

    String cron();


    /**
     * --------------------- 任务配置 ---------------------
     */
    String scheduleType() default XxlJobConstant.SCHEDULE_TYPE_CRON;

    String executorParam() default "";

    /**
     * --------------------- 高级配置 ---------------------
     */

    ExecutorRouteStrategyEnum route() default ExecutorRouteStrategyEnum.FIRST;

    String scheduleMisfire() default XxlJobConstant.SCHEDULE_MISFIRE_DO_NOTHING;

    // 阻塞处理策略
    String executorBlockStrategy() default "SERIAL_EXECUTION";

    // 任务执行超时时间，单位秒
    int executorTimeout() default 0;

    // 失败重试次数
    int executorFailRetryCount() default 0;

    String author() default "admin";

    //子任务
    String childJob() default "";
}
