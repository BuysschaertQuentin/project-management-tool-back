package com.iscod.project_management_tool_back.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iscod.project_management_tool_back.entity.Task;
import com.iscod.project_management_tool_back.entity.pmtenum.TaskStatusEnum;

@Repository
public interface ITaskRepository extends JpaRepository<Task, Long> {

    @Query("SELECT t FROM Task t " +
           "LEFT JOIN FETCH t.project " +
           "LEFT JOIN FETCH t.assignedTo " +
           "LEFT JOIN FETCH t.createdBy " +
           "WHERE t.project.id = :projectId " +
           "ORDER BY t.createdAt DESC")
    List<Task> findByProjectIdOrderByCreatedAtDesc(@Param("projectId") Long projectId);

    @Query("SELECT t FROM Task t " +
           "LEFT JOIN FETCH t.project " +
           "LEFT JOIN FETCH t.assignedTo " +
           "LEFT JOIN FETCH t.createdBy " +
           "WHERE t.project.id = :projectId AND t.status = :status")
    List<Task> findByProjectIdAndStatus(@Param("projectId") Long projectId, @Param("status") TaskStatusEnum status);

    @Query("SELECT t FROM Task t " +
           "LEFT JOIN FETCH t.project " +
           "LEFT JOIN FETCH t.assignedTo " +
           "LEFT JOIN FETCH t.createdBy " +
           "WHERE t.id = :id")
    Optional<Task> findByIdWithRelations(@Param("id") Long id);

    List<Task> findByProjectId(Long projectId);

    List<Task> findByAssignedToId(Long userId);
}
