package com.iscod.project_management_tool_back.repository;

import com.iscod.project_management_tool_back.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IProjectRepository extends JpaRepository<Project, Long> {
}
