package com.iscod.project_management_tool_back.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iscod.project_management_tool_back.entity.ProjectMember;

@Repository
public interface IProjectMemberRepository extends JpaRepository<ProjectMember, Long> {

    boolean existsByProjectIdAndUserId(Long projectId, Long userId);

    Optional<ProjectMember> findByProjectIdAndUserId(Long projectId, Long userId);

    @Query("SELECT pm FROM ProjectMember pm " +
           "LEFT JOIN FETCH pm.project " +
           "LEFT JOIN FETCH pm.user " +
           "WHERE pm.project.id = :projectId")
    List<ProjectMember> findByProjectId(@Param("projectId") Long projectId);

    @Query("SELECT pm FROM ProjectMember pm " +
           "LEFT JOIN FETCH pm.project " +
           "LEFT JOIN FETCH pm.user " +
           "WHERE pm.id = :id")
    Optional<ProjectMember> findByIdWithRelations(@Param("id") Long id);

    @Query("SELECT pm FROM ProjectMember pm " +
           "LEFT JOIN FETCH pm.project p " +
           "LEFT JOIN FETCH p.createdBy " +
           "LEFT JOIN FETCH pm.user " +
           "WHERE pm.user.id = :userId")
    List<ProjectMember> findByUserId(@Param("userId") Long userId);
}
