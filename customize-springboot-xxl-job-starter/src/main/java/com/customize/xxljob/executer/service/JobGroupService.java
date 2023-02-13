package com.customize.xxljob.executer.service;


import com.customize.xxljob.executer.model.XxlJobGroup;

import java.util.List;
import java.util.Set;

public interface JobGroupService {

    /**
     * 初始化任务执行器组
     * @param
     * @author zhangxu
     * @date 2023/2/12
     * @return
     */
    void initJobGroup();

    /**
     * 自动检测 任务执行器组是否存在，不存在则注册
     * @param group
     * @author zhangxu
     * @date 2023/2/13
     * @return
     */
    Set<XxlJobGroup> autoCheckAndRegisterGroup(List<XxlJobGroup> group);

}
