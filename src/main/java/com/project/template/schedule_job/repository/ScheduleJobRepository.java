package com.project.template.schedule_job.repository;


import com.project.template.schedule_job.entity.ScheduleJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleJobRepository extends JpaRepository<ScheduleJob, Long> {
    List<ScheduleJob> findByStatusAndIsDeleted(Integer status, Integer isDeleted);
}
