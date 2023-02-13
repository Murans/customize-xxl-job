package com.customize.xxljob.executer.service;


import com.customize.xxljob.executer.model.XxlJobInfo;

import java.util.List;

public interface JobInfoService {

    List<XxlJobInfo> getJobInfo(Integer jobGroupId, String executorHandler, String jobDesc);

    Integer addJobInfo(XxlJobInfo xxlJobInfo);

}
