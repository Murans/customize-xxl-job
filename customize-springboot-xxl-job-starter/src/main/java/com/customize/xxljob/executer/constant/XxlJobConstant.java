package com.customize.xxljob.executer.constant;

/**
 * xxl-job 调度类型枚举
 *
 * @Author zhangxu
 * @Date 2023/2/13
 **/
public interface XxlJobConstant {

    /**
     * 自动注册
     */
    String JOB_REGISTER_AUTO = "auto";

    /**
     * 手动注册
     */
    String JOB_REGISTER_MANUAL = "manual";

    /**
     * 调度类型-cron表达式
     */
    String SCHEDULE_TYPE_CRON = "CRON";
    /**
     * 调度类型 -定速
     */
    String SCHEDULE_TYPE_FIXED_RATE = "FIXED_RATE";

    /**
     * 调度过期策略 -忽略
     */
    String SCHEDULE_MISFIRE_DO_NOTHING = "DO_NOTHING";

    /**
     * 调度过期策略 -执行一次
     */
    String SCHEDULE_MISFIRE_IGNORE_MISFIRES = "FIRE_ONCE_NOW";
}
