package com.customize.xxljob.executer.service;


import com.customize.xxljob.executer.model.XxlJobInfo;

import java.util.List;

public interface JobInfoService {
    /**
     * 获取任务信息
     *
     * @param jobGroupId
     * @param executorHandler
     * @param jobDesc
     * @return
     * @author zhangxu
     * @date 2023/2/13
     */
    List<XxlJobInfo> getJobInfo(Integer jobGroupId, String executorHandler, String jobDesc);


    /**
     * 添加任务
     *
     * @param xxlJobInfo
     * @return
     * @author zhangxu
     * @date 2023/2/13
     */
    Integer addJobInfo(XxlJobInfo xxlJobInfo);

}
