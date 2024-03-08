package com.project.template.schedule_job.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ScheduleJobException extends RuntimeException{

    public ScheduleJobException(String message) {
        super(message);
    }

}
