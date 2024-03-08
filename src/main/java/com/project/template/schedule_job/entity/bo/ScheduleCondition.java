package com.project.template.schedule_job.entity.bo;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors
public class ScheduleCondition {

    private String className;

    private String functionName;

    private String cron;

    private Integer isConcurrency;

    private Integer status;

    private Integer isDeleted;

    private Integer pageNum;

    private Integer pageSize;

}
