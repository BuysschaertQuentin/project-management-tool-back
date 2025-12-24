package com.iscod.project_management_tool_back.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.iscod.project_management_tool_back.entity.PmtUser;
import com.iscod.project_management_tool_back.entity.Task;
import com.iscod.project_management_tool_back.service.IEmailService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements IEmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);
    
    private final JavaMailSender mailSender;
    
    @Value("${app.mail.from:noreply@pmt.com}")
    private String fromEmail;
    
    @Value("${app.mail.enabled:false}")
    private boolean mailEnabled;

    /**
     * Sends a task assignment notification email (US11).
     * Email is sent asynchronously to avoid blocking the main thread.
     */
    @Override
    @Async
    public void sendTaskAssignmentEmail(PmtUser assignee, Task task) {
        String subject = "[PMT] Nouvelle tâche assignée : " + task.getName();
        String body = buildTaskAssignmentEmailBody(assignee, task);
        
        sendEmail(assignee.getEmail(), subject, body);
    }

    @Override
    @Async
    public void sendProjectInvitationEmail(PmtUser invitee, String projectName, String inviterName) {
        String subject = "[PMT] Invitation au projet : " + projectName;
        String body = String.format(
            """
            Bonjour %s,

            %s vous a invité à rejoindre le projet "%s".

            Connectez-vous à PMT pour accéder au projet et commencer à collaborer avec votre équipe.

            Cordialement,
            L'équipe PMT
            """,
            invitee.getUsername(), inviterName, projectName
        );
        
        sendEmail(invitee.getEmail(), subject, body);
    }

    @Override
    @Async
    public void sendTaskUpdateEmail(PmtUser user, Task task, String changes) {
        String subject = "[PMT] Tâche mise à jour : " + task.getName();
        String body = String.format(
            """
            Bonjour %s,

            La tâche "%s" a été mise à jour.

            Modifications : %s

            Détails de la tâche :
            - Projet : %s
            - Statut : %s
            - Priorité : %s

            Connectez-vous à PMT pour voir les détails complets.

            Cordialement,
            L'équipe PMT
            """,
            user.getUsername(), task.getName(), changes,
            task.getProject().getName(),
            task.getStatus().getStatus(),
            task.getPriority().getPriority()
        );
        
        sendEmail(user.getEmail(), subject, body);
    }

    /**
     * Core method to send an email.
     * If mail is disabled, logs the email instead of sending.
     */
    private void sendEmail(String to, String subject, String body) {
        if (!mailEnabled) {
            logger.info("=== EMAIL (DISABLED - Not sent) ===");
            logger.info("To: {}", to);
            logger.info("Subject: {}", subject);
            logger.info("Body:\n{}", body);
            logger.info("===================================");
            logger.info("To enable email sending, set MAIL_ENABLED=true and configure SMTP settings");
            return;
        }
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            
            mailSender.send(message);
            
            logger.info("Email sent successfully to: {}", to);
            logger.debug("Subject: {}", subject);
        } catch (MailException e) {
            logger.error("Failed to send email to {}: {}", to, e.getMessage());
            // Don't throw - email failure should not break the main flow
        }
    }

    private String buildTaskAssignmentEmailBody(PmtUser assignee, Task task) {
        return String.format(
            """
            Bonjour %s,

            Une nouvelle tâche vous a été assignée dans le projet "%s".

            Détails de la tâche :
            - Nom : %s
            - Description : %s
            - Priorité : %s
            - Date d'échéance : %s

            Connectez-vous à PMT pour voir les détails complets et commencer à travailler sur cette tâche.

            Cordialement,
            L'équipe PMT
            """,
            assignee.getUsername(),
            task.getProject().getName(),
            task.getName(),
            task.getDescription() != null ? task.getDescription() : "Aucune description",
            task.getPriority().getPriority(),
            task.getDueDate() != null ? task.getDueDate().toString() : "Non définie"
        );
    }
}
