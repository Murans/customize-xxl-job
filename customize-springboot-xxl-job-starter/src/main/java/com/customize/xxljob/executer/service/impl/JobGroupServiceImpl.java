package com.customize.xxljob.executer.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.customize.xxljob.executer.model.XxlJobGroup;
import com.customize.xxljob.executer.service.JobGroupService;
import com.customize.xxljob.executer.service.JobLoginService;
import com.xxl.job.core.biz.AdminBiz;
import com.xxl.job.core.biz.client.AdminBizClient;
import com.xxl.job.core.biz.model.RegistryParam;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.enums.RegistryConfig;
import com.xxl.job.core.util.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Slf4j
@Service
public class JobGroupServiceImpl implements JobGroupService {

    /**
     * 执行器组集合
     */
    private final Set<XxlJobGroup> listGroup = new HashSet<>();
    @Value("${xxl.job.admin.addresses:}")
    private String adminAddresses;
    @Value("${xxl.job.executor.port:}")
    private int port;
    @Autowired
    private JobLoginService jobLoginService;

    /**
     * 项目启动获取所有已经注册过的执行器
     */
    @Override
    public void initJobGroup() {
        String url = adminAddresses + "/jobgroup/pageList";

        HttpResponse response = HttpRequest.post(url)
                .form("start", "0")
                .form("length", Integer.MAX_VALUE)
                .cookie(jobLoginService.getCookie())
                .execute();

        if (response.getStatus() == HttpStatus.HTTP_OK) {

            //执行器组集合
            listGroup.addAll(JSONUtil.parse(response.body())
                    .getByPath("data", JSONArray.class)
                    .stream()
                    .map(o -> JSONUtil.toBean((JSONObject) o, XxlJobGroup.class))
                    .collect(Collectors.toList()));

            //遍历执行器组集合，注册到xxl-job-admin,
            listGroup.stream().collect(Collectors.toSet()).forEach(group -> {
                AdminBiz adminBiz = new AdminBizClient(adminAddresses, "default_token");

                String ip = IpUtil.getIp();
                String registryValue = ip + ":" + port;
                RegistryParam registryParam = new RegistryParam(RegistryConfig.RegistType.EXECUTOR.name(), group.getAppname(), registryValue);
                ReturnT<String> returnT = adminBiz.registry(registryParam);
                if (returnT.getCode() == HttpStatus.HTTP_OK) {
                    log.info("执行器注册成功，执行器组：{}，执行器映射地址：{}", group.getAppname(), registryValue);
                } else {
                    log.error("执行器注册失败，执行器组：{}，执行器映射地址：{}", group.getAppname(), registryValue);
                }
            });
        }


    }

    /**
     * 校验待自动注册的执行器是否已经创建过，未创建则自动创建
     *
     * @return
     * @author zhangxu
     * @date 2023/2/13
     */
    @Override
    public Set<XxlJobGroup> autoCheckAndRegisterGroup(List<XxlJobGroup> group) {
        if (CollectionUtil.isEmpty(listGroup)) {
            initJobGroup();
        }
        //待自动注册的执行器
        List<XxlJobGroup> autoRegisterList = new ArrayList<>();
        for (XxlJobGroup jobGroup : group) {
            AtomicBoolean flag = new AtomicBoolean(true);

            listGroup.stream().forEach(xxlJobGroup -> {
                if (jobGroup.getAppname().equals(xxlJobGroup.getAppname())
                        && jobGroup.getTitle().equals(xxlJobGroup.getTitle())) {
                    //注册过
                    flag.set(false);
                }
            });
            //未注册过的 执行器
            if (flag.get()) {
                autoRegisterList.add(jobGroup);
            }
        }
        if (CollectionUtil.isNotEmpty(autoRegisterList)) {
            //遍历待自动注册的执行器，自动注册
            for (XxlJobGroup a : autoRegisterList) {
                HttpResponse response = HttpRequest.post(adminAddresses + "/jobgroup/save")
                        .form("appname", a.getAppname())
                        .form("title", a.getTitle())
                        .form("addressType", a.getAddressType())
                        .form("addressList", a.getRegistryList())
                        .cookie(jobLoginService.getCookie())
                        .execute();

                if (response.getStatus() == HttpStatus.HTTP_OK) {
                    Object code = JSONUtil.parse(response.body()).getByPath("code");
                    if (!code.equals(200)) {
                        throw new RuntimeException("自动注册执行器失败！");
                    }
                }

            }
            initJobGroup();
        }
        return listGroup;
    }

}
