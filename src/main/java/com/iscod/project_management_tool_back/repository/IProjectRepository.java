package com.iscod.project_management_tool_back.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iscod.project_management_tool_back.entity.Project;

@Repository
public interface IProjectRepository extends JpaRepository<Project, Long> {

    @Query("SELECT p FROM Project p " +
           "LEFT JOIN FETCH p.createdBy " +
           "WHERE p.id = :id")
    Optional<Project> findByIdWithRelations(@Param("id") Long id);
}
