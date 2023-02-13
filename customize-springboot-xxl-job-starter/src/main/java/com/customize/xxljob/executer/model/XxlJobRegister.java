package com.customize.xxljob.executer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
/**
 * 自动注册任务信息
 * @author zhangxu
 * @date 2023/2/13
 * @return
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class XxlJobRegister {

   private   List<XxlJobInfo> listJobInfo;

    private  List<XxlJobGroup> listGroup;

}
