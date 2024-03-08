package com.project.template.schedule_job.service;

import com.project.template.schedule_job.core.job.ConcurrencyDynamicJob;
import com.project.template.schedule_job.core.job.NoConcurrencyDynamicJob;
import com.project.template.schedule_job.entity.ScheduleJob;
import com.project.template.schedule_job.exception.ScheduleJobException;
import com.project.template.schedule_job.repository.ScheduleJobRepository;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ScheduleTemplateService {

    private final ScheduleJobRepository scheduleJobRepository;

    private final Scheduler scheduler;

    @Autowired
    public ScheduleTemplateService(ScheduleJobRepository scheduleJobRepository,
                                   Scheduler scheduler) {
        this.scheduleJobRepository = scheduleJobRepository;
        this.scheduler = scheduler;
    }

    private static final String MSG_1 = "Schedule job id:{} not exist, Please create schedule job";
    private static final String MSG_2 = "Schedule job class:{},function:{} not exist, Please create schedule job";

    /**
     * 添加定时任务
     *
     * @param className     类名
     * @param functionName  函数名称
     * @param cron          cron表达式字符串
     * @param isConcurrency 是否并发 0否 1是
     * @return {@link ScheduleJob}
     */
    public ScheduleJob add(String className, String functionName, String cron, int isConcurrency) {
        // 判断是否存在className和function的任务如果存在，则不允许添加
        ScheduleJob scheduleJob = scheduleJobRepository.findByClassNameAndFunctionNameAndIsDeleted(className, functionName, 0);
        if (scheduleJob != null) {
            log.info("this class and function job already exist");
            return scheduleJob;
        }
        scheduleJob = new ScheduleJob()
                .setClassName(className)
                .setFunctionName(functionName)
                .setCron(cron)
                .setIsConcurrency(isConcurrency);
        scheduleJob = scheduleJobRepository.save(scheduleJob);
        // 初始化定时任务JobData
        JobDataMap jobDataMap = this.initJobData(className, functionName);
        JobBuilder jobBuilder;
        // 确实是否支持并发
        if (isConcurrency == 0) {
            jobBuilder = JobBuilder.newJob(NoConcurrencyDynamicJob.class);
        } else {
            jobBuilder = JobBuilder.newJob(ConcurrencyDynamicJob.class);
        }
        CronScheduleBuilder cronScheduleBuilder = this.cronBuild(cron);
        JobDetail jobDetail = jobBuilder
                .withIdentity(scheduleJob.getId().toString())
                .usingJobData(jobDataMap)
                .build();
        CronTrigger cronTrigger = TriggerBuilder.newTrigger()
                .withIdentity(scheduleJob.getId().toString())
                // 立即执行
                .startNow()
                .withSchedule(cronScheduleBuilder)
                .build();
        try {
            // 添加定时任务
            scheduler.scheduleJob(jobDetail, cronTrigger);
        } catch (SchedulerException e) {
            throw new ScheduleJobException("schedule add failed");
        }
        return scheduleJob;
    }

    /**
     * 暂停定时任务
     *
     * @param id 编号
     * @return boolean true：成功 false：失败
     */
    public boolean pause(Long id) {
        // 判断改定时任务是否存在
        ScheduleJob scheduleJob = scheduleJobRepository.findById(id).orElse(null);
        if (scheduleJob == null) {
            log.info(MSG_1, id);
            return false;
        }
        return pause(scheduleJob);
    }

    /**
     * 暂停定时任务
     *
     * @param className    类名
     * @param functionName 函数名称
     * @return boolean true：成功 false：失败
     */
    public boolean pause(String className, String functionName) {
        // 判断改定时任务是否存在
        ScheduleJob scheduleJob = scheduleJobRepository.findByClassNameAndFunctionNameAndIsDeleted(className, functionName, 0);
        if (scheduleJob == null) {
            log.info(MSG_2, className, functionName);
            return false;
        }
        return pause(scheduleJob);
    }

    /**
     * 暂停定时任务
     *
     * @param scheduleJob 定时任务对象
     * @return boolean true：成功 false：失败
     */
    private boolean pause(ScheduleJob scheduleJob) {
        JobKey jobKey = JobKey.jobKey(scheduleJob.getId().toString());
        try {
            scheduler.pauseJob(jobKey);
        } catch (SchedulerException e) {
            throw new ScheduleJobException(e.getLocalizedMessage());
        }
        // 确保任务成功停止之后更新数据库
        scheduleJob.setStatus(0);
        scheduleJobRepository.save(scheduleJob);
        return true;
    }

    /**
     * 删除定时任务
     *
     * @param id 编号
     * @return boolean true：成功 false：失败
     */
    public boolean delete(Long id) {
        ScheduleJob scheduleJob = scheduleJobRepository.findById(id).orElse(null);
        if (scheduleJob == null) {
            log.info(MSG_1, id);
            return false;
        }
        return delete(scheduleJob);
    }

    /**
     * 删除定时任务
     *
     * @param className    类名
     * @param functionName 函数名称
     * @return boolean true：成功 false：失败
     */
    public boolean delete(String className, String functionName) {
        ScheduleJob scheduleJob = scheduleJobRepository.findByClassNameAndFunctionNameAndIsDeleted(className, functionName, 0);
        if (scheduleJob == null) {
            log.info(MSG_2, className, functionName);
            return false;
        }
        return delete(scheduleJob);
    }

    /**
     * 删除定时任务
     *
     * @param scheduleJob 定时任务对象
     * @return boolean true：成功 false：失败
     */
    private boolean delete(ScheduleJob scheduleJob) {
        JobKey jobKey = JobKey.jobKey(scheduleJob.getId().toString());
        try {
            scheduler.deleteJob(jobKey);
        } catch (SchedulerException e) {
            throw new ScheduleJobException(e.getLocalizedMessage());
        }
        scheduleJob.setIsDeleted(1);
        scheduleJobRepository.save(scheduleJob);
        return true;
    }

    /**
     * 恢复定时任务
     *
     * @param id 编号
     * @return boolean true：成功 false：失败
     */
    public boolean resume(Long id) {
        // 判断改定时任务是否存在
        ScheduleJob scheduleJob = scheduleJobRepository.findById(id).orElse(null);
        if (scheduleJob == null) {
            log.info(MSG_1, id);
            return false;
        }
        return resume(scheduleJob);
    }

    /**
     * 恢复定时任务
     *
     * @param className    类名
     * @param functionName 函数名称
     * @return boolean true：成功 false：失败
     */
    public boolean resume(String className, String functionName) {
        // 判断改定时任务是否存在
        ScheduleJob scheduleJob = scheduleJobRepository.findByClassNameAndFunctionNameAndIsDeleted(className, functionName, 0);
        if (scheduleJob == null) {
            log.info(MSG_2, className, functionName);
            return false;
        }
        return resume(scheduleJob);
    }


    /**
     * 恢复定时任务
     *
     * @param scheduleJob 定时任务对象
     * @return boolean true：成功 false：失败
     */
    private boolean resume(ScheduleJob scheduleJob) {
        JobKey jobKey = JobKey.jobKey(scheduleJob.getId().toString());
        try {
            scheduler.resumeJob(jobKey);
        } catch (SchedulerException e) {
            throw new ScheduleJobException(e.getLocalizedMessage());
        }
        scheduleJob.setStatus(1);
        scheduleJobRepository.save(scheduleJob);
        return true;
    }

    /**
     * 初始化定时任务数据
     *
     * @param className    类名
     * @param functionName 函数名称
     * @return {@link JobDataMap}
     */
    private JobDataMap initJobData(String className, String functionName) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("className", className);
        jobDataMap.put("functionName", functionName);
        return jobDataMap;
    }

    /**
     * 构建cron
     *
     * @param cron cron表达式字符串
     * @return {@link CronScheduleBuilder}
     */
    private CronScheduleBuilder cronBuild(String cron) {
        try {
            return CronScheduleBuilder.cronSchedule(cron);
        } catch (Exception e) {
            throw new ScheduleJobException("cron creation failed");
        }
    }

}
