package com.example.xxljob.demo;



import com.customize.xxljob.executer.model.XxlJobInfo;


import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum XxlJobInfoEnum {
    JOB1("xxlJob1","第一个任务",""),
    JOB2("xxlJob2","第2个任务",""),
    ;

    private String jobHandler;
    private String jobDesc;
    private String executorParam;

    public static List<XxlJobInfo> getJobInfo(){
        return Stream.of(XxlJobInfoEnum.values()).map(a->{
            XxlJobInfo vo = new XxlJobInfo();
            vo.setExecutorHandler(a.jobHandler);
            vo.setJobDesc(a.jobDesc);
            vo.setExecutorParam(a.executorParam);
            vo.setExecutorTimeout(0);
            vo.setExecutorFailRetryCount(0);
            vo.setGlueRemark("GLUE代码初始化");
            vo.setExecutorBlockStrategy("SERIAL_EXECUTION");
            vo.setMisfireStrategy("DO_NOTHING");
            vo.setGlueType("BEAN");
            vo.setTriggerStatus(0);
            return vo;
        }).collect(Collectors.toList());
    }

    XxlJobInfoEnum(String jobHandler, String jobDesc, String executorParam) {
        this.jobHandler = jobHandler;
        this.jobDesc = jobDesc;
        this.executorParam = executorParam;
    }

    public String getJobHandler() {
        return jobHandler;
    }

    public void setJobHandler(String jobHandler) {
        this.jobHandler = jobHandler;
    }

    public String getJobDesc() {
        return jobDesc;
    }

    public void setJobDesc(String jobDesc) {
        this.jobDesc = jobDesc;
    }

    public String getExecutorParam() {
        return executorParam;
    }

    public void setExecutorParam(String executorParam) {
        this.executorParam = executorParam;
    }
}
