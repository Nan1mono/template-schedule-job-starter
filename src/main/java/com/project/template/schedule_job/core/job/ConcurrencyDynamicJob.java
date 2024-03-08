package com.project.template.schedule_job.core.job;

import com.project.template.schedule_job.core.initializer.SchedulerInitializer;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class ConcurrencyDynamicJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        SchedulerInitializer.execute(jobExecutionContext);
    }

}
