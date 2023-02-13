package com.customize.xxljob.executer.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.xxl.job.core.biz.AdminBiz;
import com.xxl.job.core.biz.client.AdminBizClient;
import com.xxl.job.core.biz.model.RegistryParam;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.enums.RegistryConfig;
import com.xxljob.sunlong.executor.model.XxlJobGroup;
import com.xxljob.sunlong.executor.service.JobGroupService;
import com.xxljob.sunlong.executor.service.JobLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class JobGroupServiceImpl implements JobGroupService {

    @Value("${xxl.job.admin.addresses}")
    private String adminAddresses;


    @Autowired
    private JobLoginService jobLoginService;

    private final Set<XxlJobGroup> listGroup = new HashSet<>();

    /**
     * 项目启动获取所有已经注册过的执行器
     */
    @Override
    public void initJobGroup() {
        HttpResponse response = HttpRequest.post(adminAddresses+"/jobgroup/pageList")
                .form("start", "0")
                .form("length", "5000")
                .cookie(jobLoginService.getCookie())
                .execute();
        listGroup.addAll(JSONUtil.parse(response.body())
                .getByPath("data", JSONArray.class)
                .stream()
                .map(o -> JSONUtil.toBean((JSONObject) o, XxlJobGroup.class))
                .collect(Collectors.toList()));

        listGroup.forEach(group ->{
            AdminBiz adminBiz = new AdminBizClient(adminAddresses, "default_token");

            RegistryParam registryParam = new RegistryParam(RegistryConfig.RegistType.EXECUTOR.name(), group.getAppname(), "127.0.0.1:9991");
            ReturnT<String> returnT = adminBiz.registry(registryParam);
        });



    }

    /**
     * 校验待自动注册的执行器是否已经创建过，未创建则自动创建
     * @return
     */
    @Override
    public Set<XxlJobGroup> autoCheckAndRegisterGroup(List<XxlJobGroup> group) {
        if (CollectionUtil.isEmpty(listGroup)){
            initJobGroup();
        }
        //待自动注册的执行器
        List<XxlJobGroup> list1 = new ArrayList<>();

        for (XxlJobGroup jobGroup : group) {
            boolean flag = true;
            for (XxlJobGroup xxlJobGroup : listGroup) {
                if (jobGroup.getAppname().equals(xxlJobGroup.getAppname())
                &&jobGroup.getTitle().equals(xxlJobGroup.getTitle())){
                    //注册过
                    flag =false;
                    break;
                }
            }
            if (flag){
                list1.add(jobGroup);
            }
        }
        if (CollectionUtil.isNotEmpty(list1)){
            for (XxlJobGroup a : list1) {
                HttpResponse response = HttpRequest.post(adminAddresses+"/jobgroup/save")
                        .form("appname", a.getAppname())
                        .form("title", a.getTitle())
                        .form("addressType", a.getAddressType())
                        .form("addressList", a.getRegistryList())
                        .cookie(jobLoginService.getCookie())
                        .execute();
                Object code = JSONUtil.parse(response.body()).getByPath("code");
                if (!code.equals(200)){
                    throw new RuntimeException("自动注册执行器失败！");
                }
            }
            initJobGroup();
        }
        return listGroup;
    }

}
