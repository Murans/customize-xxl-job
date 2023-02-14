package com.customize.xxljob.executer.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;

import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import com.customize.xxljob.executer.service.JobLoginService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.NamedThreadLocal;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class JobLoginServiceImpl implements JobLoginService {

    private static final String COOKIE_KEY = "XXL_JOB_LOGIN_IDENTITY";
    private final ThreadLocal<Map<String, String>> loginCookie = new NamedThreadLocal<>(COOKIE_KEY);

    @Value("${xxl.job.admin.addresses:}")
    private String adminAddresses;
    @Value("${xxl.job.admin.username:}")
    private String username;
    @Value("${xxl.job.admin.password:}")
    private String password;

    @Override
    @PostConstruct
    public Map<String, String> loginAndGetCookie() {
        Map<String, String> resultMap = new HashMap<>();
        String[] split = adminAddresses.split(",");

        Arrays.asList(split).stream().forEach(address -> {
            String logUrl = address + "/login";
            HttpResponse response = HttpRequest.post(logUrl)
                    .form("userName", username)
                    .form("password", password)
                    .execute();

            //登录成功
            if (response.getStatus() == HttpStatus.HTTP_OK) {
                String cookie = response.getCookies().stream()
                        .filter(a -> a.getName().equals(COOKIE_KEY))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("get xxl-job cookie error! url: " + logUrl))
                        .getValue();
                resultMap.put(address, COOKIE_KEY + "=" + cookie);
            } else {
                log.error("get xxl-job cookie error! by loginAndGetCookie method. url:{}", logUrl);
                throw new RuntimeException("get xxl-job cookie error! by loginAndGetCookie method");
            }
        });
        loginCookie.set(resultMap);
        return resultMap;
    }

    @Override
    public String loginAndGetCookie(String adminAddresses) {
        Map<String, String> resultMap = new HashMap<>();
        String logUrl = adminAddresses + "/login";
        HttpResponse response = HttpRequest.post(logUrl)
                .form("userName", username)
                .form("password", password)
                .execute();

        //登录成功
        if (response.getStatus() == HttpStatus.HTTP_OK) {
            String cookie = response.getCookies().stream()
                    .filter(a -> a.getName().equals(COOKIE_KEY))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("get xxl-job cookie error! url: " + logUrl))
                    .getValue();

            resultMap.put(adminAddresses, COOKIE_KEY + "=" + cookie);
            loginCookie.set(resultMap);

            return COOKIE_KEY + "=" + cookie;
        } else {
            log.error("get xxl-job cookie error! by loginAndGetCookie method. url:{}", logUrl);
            throw new RuntimeException("get xxl-job cookie error! by loginAndGetCookie method");
        }

    }


    @Override
    public String getCookie(String address) {

        Map<String, String> cookieMap = loginCookie.get();
        if (cookieMap != null) {
            String key = cookieMap.keySet().stream()
                    .filter(e -> e.equals(address)).findFirst().get();
            return cookieMap.get(key);
        } else {
            return loginAndGetCookie(address);
        }
    }


}
