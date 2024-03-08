package com.project.template.schedule_job.service;

import com.project.template.schedule_job.entity.ScheduleJob;
import com.project.template.schedule_job.entity.bo.ScheduleCondition;
import com.project.template.schedule_job.repository.ScheduleJobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduleDataTemplate {

    private final ScheduleJobRepository scheduleJobRepository;

    @Autowired
    public ScheduleDataTemplate(ScheduleJobRepository scheduleJobRepository) {
        this.scheduleJobRepository = scheduleJobRepository;
    }

    public List<ScheduleJob> find(ScheduleCondition condition) {
        return scheduleJobRepository
                .findByClassNameContainingAndFunctionNameContainingAndIsConcurrencyAndStatusAndIsDeleted(
                        condition.getClassName(),
                        condition.getFunctionName(),
                        condition.getIsConcurrency(),
                        condition.getStatus(), 0);
    }

    public Page<ScheduleJob> page(ScheduleCondition condition) {
        PageRequest pageRequest = PageRequest.of(condition.getPageNum(), condition.getPageSize());
        return scheduleJobRepository.findByClassNameContainingAndFunctionNameContainingAndIsConcurrencyAndStatusAndIsDeleted(
                condition.getClassName(),
                condition.getFunctionName(),
                condition.getIsConcurrency(),
                condition.getStatus(), 1, pageRequest);
    }

    public ScheduleJob find(Long id) {
        return scheduleJobRepository.findById(id).orElse(null);
    }
}
