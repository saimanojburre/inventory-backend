package com.inventory.system.activity.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inventory.system.activity.entity.ActivityLog;
import com.inventory.system.activity.enums.ActionType;
import com.inventory.system.activity.enums.ModuleType;

public interface ActivityLogRepository
        extends JpaRepository<ActivityLog, Long> {

    // =====================================================
    // USER LOGS
    // =====================================================

    List<ActivityLog> findByUserIdOrderByActivityTimeDesc(
            Long userId
    );

    // =====================================================
    // MODULE LOGS
    // =====================================================

    List<ActivityLog> findByModuleOrderByActivityTimeDesc(
            ModuleType module
    );

    // =====================================================
    // ACTION LOGS
    // =====================================================

    List<ActivityLog> findByActionOrderByActivityTimeDesc(
            ActionType action
    );

    // =====================================================
    // SESSION LOGS
    // =====================================================

    List<ActivityLog> findBySessionIdOrderByActivityTimeAsc(
            String sessionId
    );

    // =====================================================
    // DATE RANGE
    // =====================================================

    List<ActivityLog> findByActivityTimeBetweenOrderByActivityTimeDesc(
            LocalDateTime start,
            LocalDateTime end
    );

    // =====================================================
    // ALL LOGS
    // =====================================================

    List<ActivityLog> findAllByOrderByActivityTimeDesc();
}