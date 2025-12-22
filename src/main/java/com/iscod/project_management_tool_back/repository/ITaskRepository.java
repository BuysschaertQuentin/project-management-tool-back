package com.iscod.project_management_tool_back.repository;

import com.iscod.project_management_tool_back.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ITaskRepository extends JpaRepository<Task, Long> {
}

