package com.customize.xxljob.executer.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.customize.xxljob.executer.model.XxlJobInfo;
import com.customize.xxljob.executer.service.JobInfoService;
import com.customize.xxljob.executer.service.JobLoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class JobInfoServiceImpl implements JobInfoService {

    @Value("${xxl.job.admin.addresses:}")
    private String adminAddresses;

    @Autowired
    private JobLoginService jobLoginService;

    @Override
    public List<XxlJobInfo> getJobInfo(Integer jobGroupId, String executorHandler, String jobDesc) {
        String address = Arrays.stream(adminAddresses.split(",")).findFirst().get();
        String jobListUrl = address + "/jobinfo/pageList";
        HttpResponse response = HttpRequest.post(jobListUrl)
                .form("jobGroup", jobGroupId)
                .form("executorHandler", executorHandler)
                .form("jobDesc", jobDesc)
                .form("triggerStatus", -1)
                .form("start", 0)
                .form("length", Integer.MAX_VALUE)
                .cookie(jobLoginService.getCookie(address))
                .execute();

        if (response.getStatus() == HttpStatus.HTTP_OK) {
            String body = response.body();
            JSONArray array = JSONUtil.parse(body).getByPath("data", JSONArray.class);
            List<XxlJobInfo> listData = array.stream()
                    .map(o -> JSONUtil.toBean((JSONObject) o, XxlJobInfo.class))
                    .collect(Collectors.toList());

            return listData;
        } else {
            throw new RuntimeException("get jobInfo error!");
        }
    }

    @Override
    public Integer addJobInfo(XxlJobInfo xxlJobInfo) {
        String address = Arrays.stream(adminAddresses.split(",")).findFirst().get();
        String url = address + "/jobinfo/add";
        Map<String, Object> paramMap = BeanUtil.beanToMap(xxlJobInfo);
        HttpResponse response = HttpRequest.post(url)
                .form(paramMap)
                .cookie(jobLoginService.getCookie(address))
                .execute();

        if (response.getStatus() == HttpStatus.HTTP_OK) {
            JSON json = JSONUtil.parse(response.body());
            Object code = json.getByPath("code");
            if (code.equals(HttpStatus.HTTP_OK)) {
                return Convert.toInt(json.getByPath("content"));
            }else {
                log.error("add jobInfo error! url:{}, response:{}", url,response.body());
            }
        } else {
            log.error("add jobInfo error! url:{}, response:{}", url,response.body());
            throw new RuntimeException("add jobInfo error!");
        }
        return null;
    }

}
