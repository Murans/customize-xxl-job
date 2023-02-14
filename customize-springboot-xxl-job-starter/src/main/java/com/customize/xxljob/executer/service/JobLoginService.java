package com.customize.xxljob.executer.service;


import java.util.Map;

/**
 * job登录 及获取cookie
 *
 * @author zhangxu
 * @date 2023/2/13
 */
public interface JobLoginService {

    /**
     * 登录并获取 cookie
     *
     * @return
     * @author zhangxu
     * @date 2023/2/13
     */
    Map<String,String> loginAndGetCookie();



    String loginAndGetCookie(String address);
    /**
     * 获取 cookie
     *
     * @return
     * @author zhangxu
     * @date 2023/2/13
     * @param address
     */
    String getCookie(String address);

}
