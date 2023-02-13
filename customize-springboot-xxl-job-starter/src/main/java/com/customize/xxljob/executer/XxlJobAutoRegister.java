package com.customize.xxljob.executer;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.customize.xxljob.executer.annotation.AutoRegisterJob;
import com.customize.xxljob.executer.model.XxlJobGroup;
import com.customize.xxljob.executer.model.XxlJobInfo;
import com.customize.xxljob.executer.service.JobGroupService;
import com.customize.xxljob.executer.service.JobInfoService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.Setter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class XxlJobAutoRegister implements ApplicationListener<ApplicationReadyEvent>, ApplicationContextAware {

    //需创建的任务集合
    private static final List<XxlJobInfo> executeJobList = new ArrayList<>();

    private ApplicationContext applicationContext;

    /**
     * 自动注册 任务(创建)
     */
    @Setter
    private List<XxlJobInfo> listJobInfo;

    /**
     * 自动注册执行器(创建)
     */
    @Setter
    private List<XxlJobGroup> listGroup;


    @Autowired
    private JobGroupService jobGroupService;
    @Autowired
    private JobInfoService jobInfoService;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        //注册任务
        addJobInfo();
    }

    /**
     * 服务启动 扫描@XxlJob @AutoRegisterJob 注解
     * 实现自动注册任务及,自动添加执行器
     *
     * @author zhangxu
     * @date 2023/2/13
     */
    private void addJobInfo() {

        Set<XxlJobGroup> xxlJobGroups = jobGroupService.autoCheckAndRegisterGroup(listGroup);
        if (CollectionUtil.isEmpty(xxlJobGroups)) {
            throw new RuntimeException("auto register xxl-job group failed!");
        }

        String[] beanDefinitionNames = applicationContext.getBeanNamesForType(Object.class, false, true);
        for (String beanDefinitionName : beanDefinitionNames) {
            Object bean = applicationContext.getBean(beanDefinitionName);

            Map<Method, XxlJob> annotatedMethods = MethodIntrospector.selectMethods(bean.getClass(),
                    new MethodIntrospector.MetadataLookup<XxlJob>() {
                        @Override
                        public XxlJob inspect(Method method) {
                            return AnnotatedElementUtils.findMergedAnnotation(method, XxlJob.class);
                        }
                    });
            //扫描@XxlJob 方法,进行判断 job任务
            for (Map.Entry<Method, XxlJob> methodXxlJobEntry : annotatedMethods.entrySet()) {
                Method executeMethod = methodXxlJobEntry.getKey();
                XxlJob xxlJob = methodXxlJobEntry.getValue();

                //自动注册
                if (executeMethod.isAnnotationPresent(AutoRegisterJob.class)) {
                    AutoRegisterJob autoRegister = executeMethod.getAnnotation(AutoRegisterJob.class);
                    List<XxlJobInfo> collect = listJobInfo.stream().filter(a ->
                            a.getExecutorHandler().equals(xxlJob.value())).collect(Collectors.toList());
                    if (CollectionUtil.isEmpty(collect)) {
                        throw new RuntimeException("auto register job tasks is null!");
                    } else if (collect.size() > 1) {
                        throw new RuntimeException("auto register job tasks  is repeat！");
                    }
                    XxlJobInfo info = collect.get(0);
                    Integer jobGroupId = getJobGroupId(autoRegister, xxlJobGroups);
                    info.setJobGroup(jobGroupId);
                    info.setScheduleType("CRON");
                    info.setScheduleConf(autoRegister.cron());
                    info.setChildJobId(autoRegister.childJob());
                    info.setAuthor(autoRegister.author());
                    info.setExecutorRouteStrategy(autoRegister.route().getStrategy());
                    //校验该任务是否手动配置过
                    List<XxlJobInfo> jobInfo = jobInfoService.getJobInfo(jobGroupId, xxlJob.value(), info.getJobDesc());
                    if (!jobInfo.isEmpty()) {
                        //因为是模糊查询，需要再判断一次
                        Optional<XxlJobInfo> first = jobInfo.stream()
                                .filter(xxlJobInfo -> xxlJobInfo.getExecutorHandler().equals(xxlJob.value()))
                                .findFirst();
                        if (first.isPresent()) {
                            continue;
                        }
                    }
                    executeJobList.add(info);
                }
            }
        }
        if (CollectionUtil.isNotEmpty(executeJobList)) {
            for (XxlJobInfo xxlJobInfo : executeJobList) {
                createJob(xxlJobInfo, executeJobList);
            }
        }
    }

    /**
     * 创建任务
     *
     * @param xxlJobInfo
     * @param list
     * @author zhangxu
     * @date 2023/2/13
     */
    private Integer createJob(XxlJobInfo xxlJobInfo, List<XxlJobInfo> list) {
        if (xxlJobInfo != null && xxlJobInfo.getId() != 0) {
            return xxlJobInfo.getId();
        } else {
            if (!StrUtil.isEmpty(xxlJobInfo.getChildJobId())) {
                List<Integer> childJobIds = new ArrayList<>();
                Arrays.stream(xxlJobInfo.getChildJobId().split(",")).forEach(childJobId -> {
                    XxlJobInfo childJobInfo = list.stream()
                            .filter(a -> childJobId.equals(a.getExecutorHandler()))
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("this job not fount childJobId"));
                    childJobIds.add(createJob(childJobInfo, list));
                });
                String collect = childJobIds.stream().map(String::valueOf).collect(Collectors.joining(","));
                xxlJobInfo.setChildJobId(collect);
                Integer parentJobId = jobInfoService.addJobInfo(xxlJobInfo);
                xxlJobInfo.setId(parentJobId);
                return parentJobId;
            } else {
                Integer id = jobInfoService.addJobInfo(xxlJobInfo);
                xxlJobInfo.setId(id);
                return id;
            }
        }
    }

    /**
     * 获取执行器 id
     *
     * @author zhangxu
     * @date 2023/2/13
     */
    private Integer getJobGroupId(AutoRegisterJob autoRegister, Set<XxlJobGroup> xxlJobGroups) {
        XxlJobGroup xxlJobGroup = xxlJobGroups.stream().filter(a -> a.getTitle().equals(autoRegister.groupDesc())
                && a.getAppname().equals(autoRegister.appName())).findFirst().get();

        return xxlJobGroup.getId();

    }


}
