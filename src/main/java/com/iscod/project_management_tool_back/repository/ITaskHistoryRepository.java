package com.iscod.project_management_tool_back.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iscod.project_management_tool_back.entity.TaskHistory;

@Repository
public interface ITaskHistoryRepository extends JpaRepository<TaskHistory, Long> {

    List<TaskHistory> findByTaskIdOrderByChangedAtDesc(Long taskId);
}
