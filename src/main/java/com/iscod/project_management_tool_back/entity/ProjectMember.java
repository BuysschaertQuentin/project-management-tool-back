package com.iscod.project_management_tool_back.entity;

import java.time.LocalDateTime;

import com.iscod.project_management_tool_back.entity.pmtenum.ProjectRoleEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

@Entity
@Table(name = "project_members",
        uniqueConstraints = @UniqueConstraint(columnNames = {"project_id", "user_id"}))
@Data
public class ProjectMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private PmtUser user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectRoleEnum role;

    @Column(name = "invited_at")
    private LocalDateTime invitedAt;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    @PrePersist
    protected void onCreate() {
        if (invitedAt == null) {
            invitedAt = LocalDateTime.now();
        }
    }
}