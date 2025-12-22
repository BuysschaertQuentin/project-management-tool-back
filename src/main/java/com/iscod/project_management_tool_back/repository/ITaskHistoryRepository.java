package com.iscod.project_management_tool_back.repository;

import com.iscod.project_management_tool_back.entity.TaskHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ITaskHistoryRepository extends JpaRepository<TaskHistory, Long> {

}
