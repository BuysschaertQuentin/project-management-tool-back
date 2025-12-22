package com.iscod.project_management_tool_back.repository;

import com.iscod.project_management_tool_back.entity.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
}
