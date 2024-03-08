package com.project.template.schedule_job.repository;


import com.project.template.schedule_job.entity.ScheduleJob;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleJobRepository extends JpaRepository<ScheduleJob, Long> {
    List<ScheduleJob> findByStatusAndIsDeleted(Integer status, Integer isDeleted);

    ScheduleJob findByClassNameAndFunctionNameAndIsDeleted(String className, String functionName, Integer isDeleted);

    List<ScheduleJob> findByClassNameContainingAndFunctionNameContainingAndIsConcurrencyAndStatusAndIsDeleted(String className,
                                                                                                              String functionName,
                                                                                                              int isConcurrency,
                                                                                                              int status,
                                                                                                              int isDeleted);

    Page<ScheduleJob> findByClassNameContainingAndFunctionNameContainingAndIsConcurrencyAndStatusAndIsDeleted(String className,
                                                                                                              String functionName,
                                                                                                              int isConcurrency,
                                                                                                              int status,
                                                                                                              int isDeleted,
                                                                                                              PageRequest pageRequest);
}
