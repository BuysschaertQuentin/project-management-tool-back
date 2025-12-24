package com.iscod.project_management_tool_back.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iscod.project_management_tool_back.entity.Notification;

@Repository
public interface INotificationRepository extends JpaRepository<Notification, Long> {

    @Query("SELECT n FROM Notification n " +
           "LEFT JOIN FETCH n.user " +
           "LEFT JOIN FETCH n.task " +
           "LEFT JOIN FETCH n.project " +
           "WHERE n.user.id = :userId " +
           "ORDER BY n.sentAt DESC")
    List<Notification> findByUserIdOrderBySentAtDesc(@Param("userId") Long userId);

    @Query("SELECT n FROM Notification n " +
           "LEFT JOIN FETCH n.user " +
           "LEFT JOIN FETCH n.task " +
           "LEFT JOIN FETCH n.project " +
           "WHERE n.user.id = :userId AND n.isRead = false " +
           "ORDER BY n.sentAt DESC")
    List<Notification> findByUserIdAndIsReadFalseOrderBySentAtDesc(@Param("userId") Long userId);

    @Query("SELECT n FROM Notification n " +
           "LEFT JOIN FETCH n.user " +
           "LEFT JOIN FETCH n.task " +
           "LEFT JOIN FETCH n.project " +
           "WHERE n.id = :id")
    Optional<Notification> findByIdWithRelations(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = CURRENT_TIMESTAMP WHERE n.user.id = :userId AND n.isRead = false")
    void markAllAsReadByUserId(@Param("userId") Long userId);
}
