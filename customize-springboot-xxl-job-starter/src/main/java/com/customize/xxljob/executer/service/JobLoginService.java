package com.customize.xxljob.executer.service;


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
    String loginAndGetCookie();

    /**
     * 获取 cookie
     *
     * @return
     * @author zhangxu
     * @date 2023/2/13
     */
    String getCookie();

}
