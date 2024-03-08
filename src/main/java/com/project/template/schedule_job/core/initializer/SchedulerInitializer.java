package com.project.template.schedule_job.core.initializer;

import com.project.template.schedule_job.core.job.ConcurrencyDynamicJob;
import com.project.template.schedule_job.core.job.NoConcurrencyDynamicJob;
import com.project.template.schedule_job.entity.ScheduleJob;
import com.project.template.schedule_job.repository.ScheduleJobRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

@Component
@Slf4j
public class SchedulerInitializer {

    private final ScheduleJobRepository scheduleJobRepository;

    private final Scheduler scheduler;

    @Autowired
    public SchedulerInitializer(ScheduleJobRepository scheduleJobRepository,
                                Scheduler scheduler) {
        this.scheduleJobRepository = scheduleJobRepository;
        this.scheduler = scheduler;
    }

    @PostConstruct
    public void init() throws SchedulerException {
        log.info("init schedule job ... ");
        List<ScheduleJob> jobList = scheduleJobRepository.findByStatusAndIsDeleted(1, 0);
        for (ScheduleJob job : jobList) {
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("className", job.getClassName());
            jobDataMap.put("functionName", job.getFunctionName());
            // 避免定时任务重复被初始化
            JobKey jobKey = new JobKey(job.getId().toString());
            if (scheduler.checkExists(jobKey)) {
                log.info("job already exists. Skipping ...");
                continue;
            }
            // 是否允许并发
            JobBuilder jobBuilder;
            if (job.getIsConcurrency() == 0) {
                jobBuilder = JobBuilder.newJob(NoConcurrencyDynamicJob.class);
            } else {
                jobBuilder = JobBuilder.newJob(ConcurrencyDynamicJob.class);
            }
            JobDetail jobDetail = jobBuilder
                    .withIdentity(job.getId().toString())
                    .usingJobData(jobDataMap)
                    .build();
            CronTrigger cronTrigger = TriggerBuilder.newTrigger()
                    .withIdentity(job.getId().toString())
                    .startNow()
                    .withSchedule(CronScheduleBuilder.cronSchedule(job.getCron()))
                    .build();
            scheduler.scheduleJob(jobDetail, cronTrigger);
        }
        scheduler.start();
    }

    public static void execute(JobExecutionContext jobExecutionContext) {
        JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        String className = jobDataMap.getString("className");
        String functionName = jobDataMap.getString("functionName");
        try {
            Class<?> aClass = Class.forName(className);
            Object instance = aClass.getDeclaredConstructor().newInstance();
            Method method = aClass.getMethod(functionName);
            method.invoke(instance);
        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

}
