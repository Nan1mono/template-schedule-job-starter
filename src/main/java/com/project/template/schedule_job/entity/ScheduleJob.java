package com.project.template.schedule_job.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity(name = "sys_schedule_job")
@Table(name = "sys_schedule_job")
@Accessors(chain = true)
public class ScheduleJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "class_name")
    private String className;

    @Column(name = "function_name")
    private String functionName;

    @Column(name = "cron")
    private String cron;

    @Column(name = "is_concurrency")
    private Integer isConcurrency;

    @Column(name = "status")
    private Integer status;

    @Column(name = "is_deleted")
    private Integer isDeleted;

    @Column(name = "create_by")
    private String createBy;

    @Column(name = "update_by")
    private String updateBy;

    @Column(name = "create_on")
    private LocalDateTime createOn;

    @Column(name = "update_on")
    private LocalDateTime updateOn;

}
