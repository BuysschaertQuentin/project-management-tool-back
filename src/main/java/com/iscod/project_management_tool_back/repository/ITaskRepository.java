package com.iscod.project_management_tool_back.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iscod.project_management_tool_back.entity.Task;
import com.iscod.project_management_tool_back.entity.pmtenum.TaskStatusEnum;

@Repository
public interface ITaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByProjectId(Long projectId);

    List<Task> findByProjectIdAndStatus(Long projectId, TaskStatusEnum status);

    List<Task> findByAssignedToId(Long userId);

    List<Task> findByProjectIdOrderByCreatedAtDesc(Long projectId);
}
