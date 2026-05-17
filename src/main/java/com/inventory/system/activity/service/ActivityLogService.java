package com.inventory.system.activity.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventory.system.activity.entity.ActivityLog;
import com.inventory.system.activity.enums.ActionType;
import com.inventory.system.activity.enums.ModuleType;
import com.inventory.system.activity.repository.ActivityLogRepository;

@Service
@Transactional
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    public ActivityLogService(
            ActivityLogRepository activityLogRepository
    ) {

        this.activityLogRepository =
                activityLogRepository;
    }

    // =====================================================
    // CREATE LOG
    // =====================================================

    public void log(

            Long userId,

            String username,

            String roleName,

            ModuleType module,

            ActionType action,

            String description,

            Long referenceId,

            String referenceName,

            String sessionId,

            String status
    ) {

        ActivityLog activityLog =
                new ActivityLog();

        // =====================================================
        // USER INFO
        // =====================================================

        activityLog.setUserId(userId);

        activityLog.setUsername(username);

        activityLog.setRoleName(roleName);

        // =====================================================
        // MODULE + ACTION
        // =====================================================

        activityLog.setModule(module);

        activityLog.setAction(action);

        // =====================================================
        // DESCRIPTION
        // =====================================================

        activityLog.setDescription(description);

        activityLog.setReferenceId(referenceId);

        activityLog.setReferenceName(referenceName);

        // =====================================================
        // SESSION
        // =====================================================

        activityLog.setSessionId(sessionId);

        // =====================================================
        // STATUS
        // =====================================================

        activityLog.setStatus(status);

        // =====================================================
        // TIME
        // =====================================================

        activityLog.setActivityTime(
                LocalDateTime.now()
        );

        // =====================================================
        // SAVE
        // =====================================================

        activityLogRepository.save(activityLog);
    }

    // =====================================================
    // GET ALL LOGS
    // =====================================================

    @Transactional(readOnly = true)
    public List<ActivityLog> getAllLogs() {

        return activityLogRepository
                .findAllByOrderByActivityTimeDesc();
    }

    // =====================================================
    // GET LATEST LOGS
    // =====================================================

    @Transactional(readOnly = true)
    public List<ActivityLog> getLatestLogs(
            int limit
    ) {

        List<ActivityLog> logs =
                activityLogRepository
                        .findAllByOrderByActivityTimeDesc();

        return logs.stream()
                .limit(limit)
                .toList();
    }

    // =====================================================
    // FILTER LOGS
    // =====================================================

    @Transactional(readOnly = true)
    public List<ActivityLog> filterLogs(

            String module,

            String action,

            String username
    ) {

        List<ActivityLog> logs =
                activityLogRepository
                        .findAllByOrderByActivityTimeDesc();

        return logs.stream()

                .filter(log ->

                        module == null ||

                                log.getModule()
                                        .name()
                                        .equalsIgnoreCase(module)
                )

                .filter(log ->

                        action == null ||

                                log.getAction()
                                        .name()
                                        .equalsIgnoreCase(action)
                )

                .filter(log ->

                        username == null ||

                                log.getUsername()
                                        .equalsIgnoreCase(username)
                )

                .toList();
    }
}