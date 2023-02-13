package com.customize.xxljob.executer.service;


import com.xxljob.sunlong.executor.model.XxlJobGroup;

import java.util.List;
import java.util.Set;


public interface JobGroupService {

    void initJobGroup();

    Set<XxlJobGroup> autoCheckAndRegisterGroup(List<XxlJobGroup> group);

}
