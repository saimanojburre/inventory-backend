package com.inventory.system.activity.entity;

import java.time.LocalDateTime;

import com.inventory.system.activity.enums.ActionType;
import com.inventory.system.activity.enums.ModuleType;

import jakarta.persistence.*;

@Entity
@Table(name = "activity_logs")
public class ActivityLog {

    // =====================================================
    // ID
    // =====================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =====================================================
    // USER INFO
    // =====================================================

    private Long userId;

    private String username;

    private String roleName;

    // =====================================================
    // MODULE + ACTION
    // =====================================================

    @Enumerated(EnumType.STRING)
    private ModuleType module;

    @Enumerated(EnumType.STRING)
    private ActionType action;

    // =====================================================
    // ACTIVITY DETAILS
    // =====================================================

    @Column(length = 1000)
    private String description;

    private Long referenceId;

    private String referenceName;

    // =====================================================
    // SESSION
    // =====================================================

    private String sessionId;

    // =====================================================
    // STATUS
    // =====================================================

    private String status;

    // =====================================================
    // TIMESTAMP
    // =====================================================

    private LocalDateTime activityTime;

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public ActivityLog() {
    }

    // =====================================================
    // GETTERS & SETTERS
    // =====================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public ModuleType getModule() {
        return module;
    }

    public void setModule(ModuleType module) {
        this.module = module;
    }

    public ActionType getAction() {
        return action;
    }

    public void setAction(ActionType action) {
        this.action = action;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }

    public String getReferenceName() {
        return referenceName;
    }

    public void setReferenceName(String referenceName) {
        this.referenceName = referenceName;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getActivityTime() {
        return activityTime;
    }

    public void setActivityTime(LocalDateTime activityTime) {
        this.activityTime = activityTime;
    }
}