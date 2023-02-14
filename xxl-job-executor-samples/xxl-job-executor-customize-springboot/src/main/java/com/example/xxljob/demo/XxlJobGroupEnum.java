package com.example.xxljob.demo;


import com.customize.xxljob.executer.model.XxlJobGroup;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public enum XxlJobGroupEnum {

    TEST("auto_executor1", "自动_第一个执行器", 0, ""),
    TEST2("auto_executor2", "自动_第二个执行器", 0, "");

    private String appName;
    private String groupDesc;
    private Integer addressType;//执行器地址类型：0=自动注册。1=手动录入
    private String addressList;// 执行器地址列表，多地址逗号分割，addressType=1时，才需要配置


    XxlJobGroupEnum(String appName, String groupDesc, Integer addressType, String addressList) {
        this.appName = appName;
        this.groupDesc = groupDesc;
        this.addressType = addressType;
        this.addressList = addressList;
    }

    public static List<XxlJobGroup> getJobGroup() {
        return Stream.of(XxlJobGroupEnum.values()).map(a -> {
            XxlJobGroup vo = new XxlJobGroup();
            vo.setAppname(a.appName);
            vo.setTitle(a.groupDesc);
            vo.setAddressType(a.addressType);
            vo.setAddressList(a.addressList);
            return vo;
        }).collect(Collectors.toList());
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getGroupDesc() {
        return groupDesc;
    }

    public void setGroupDesc(String groupDesc) {
        this.groupDesc = groupDesc;
    }

    public Integer getAddressType() {
        return addressType;
    }

    public void setAddressType(Integer addressType) {
        this.addressType = addressType;
    }

    public String getAddressList() {
        return addressList;
    }

    public void setAddressList(String addressList) {
        this.addressList = addressList;
    }
}
