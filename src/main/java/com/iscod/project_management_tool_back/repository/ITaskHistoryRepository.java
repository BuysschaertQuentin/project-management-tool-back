package com.iscod.project_management_tool_back.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iscod.project_management_tool_back.entity.TaskHistory;

@Repository
public interface ITaskHistoryRepository extends JpaRepository<TaskHistory, Long> {

    @Query("SELECT th FROM TaskHistory th " +
           "LEFT JOIN FETCH th.task " +
           "LEFT JOIN FETCH th.user " +
           "WHERE th.task.id = :taskId " +
           "ORDER BY th.changedAt DESC")
    List<TaskHistory> findByTaskIdOrderByChangedAtDesc(@Param("taskId") Long taskId);
}
