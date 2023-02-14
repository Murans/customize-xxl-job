package com.example.xxljob.task;

import com.customize.xxljob.executer.annotation.AutoRegisterJob;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.stereotype.Component;

/**
 * @author zhangxu
 * @Date 2023/2/13
 **/
@Component
public class xxlJobDemoTask {

    @AutoRegisterJob(executorHandler = "xxlJob1", jobDesc = "测试任务1", cron = "* * * * * ?")
    @XxlJob("xxlJob1")
    public void demoTask1() {
        long jobId = XxlJobHelper.getJobId();
        System.out.println("xxl-job xxlJob1  jobId=" + jobId   );
    }

    @AutoRegisterJob(executorHandler = "xxlJob2", jobDesc = "测试任务2", cron = "* * * * * ?")
    @XxlJob("xxlJob2")
    public void demoTask2() {
        System.out.println("xxl-job xxlJob2");
    }


    @AutoRegisterJob(executorHandler = "xxlJob3", jobDesc = "测试任务3", cron = "* * * * * ?")
    public void demoTask3() {
        System.out.println("xxl-job xxlJob3");
    }
}
