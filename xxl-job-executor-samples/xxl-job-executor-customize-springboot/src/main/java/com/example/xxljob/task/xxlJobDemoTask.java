package com.example.xxljob.task;

import com.customize.xxljob.executer.annotation.AutoRegisterJob;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.stereotype.Component;

/**
 * @author zhangxu
 * @Date 2023/2/13
 **/
@Component
public class xxlJobDemoTask {

    @AutoRegisterJob(appName = "auto_executor1",groupDesc = "第一个执行器",cron="* * * * * ?")
    @XxlJob("xxlJob1")
    public void demoTask1() {
        System.out.println("xxl-job xxlJob1");
    }

    @AutoRegisterJob(appName = "auto_executor2",groupDesc = "第二个执行器",cron="* * * * * ?")
    @XxlJob("xxlJob2")
    public void demoTask2() {
        System.out.println("xxl-job xxlJob2");
    }



    @XxlJob("xxlJob3")
    public void demoTask3() {
        System.out.println("xxl-job xxlJob3");
    }
}
