package com.inventory.system.activity.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inventory.system.activity.entity.ActivityLog;
import com.inventory.system.activity.service.ActivityLogService;

@RestController
@RequestMapping("/activity-logs")
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    public ActivityLogController(
            ActivityLogService activityLogService
    ) {

        this.activityLogService =
                activityLogService;
    }

    // =====================================================
    // GET ALL LOGS
    // =====================================================

    @GetMapping
    public ResponseEntity<List<ActivityLog>> getAllLogs() {

        return ResponseEntity.ok(
                activityLogService.getAllLogs()
        );
    }

    // =====================================================
    // GET LATEST LOGS
    // =====================================================

    @GetMapping("/latest")
    public ResponseEntity<List<ActivityLog>> getLatestLogs(

            @RequestParam(
                    defaultValue = "10"
            )
            int limit
    ) {

        return ResponseEntity.ok(
                activityLogService.getLatestLogs(limit)
        );
    }

    // =====================================================
    // FILTER LOGS
    // =====================================================

    @GetMapping("/filter")
    public ResponseEntity<List<ActivityLog>> filterLogs(

            @RequestParam(required = false)
            String module,

            @RequestParam(required = false)
            String action,

            @RequestParam(required = false)
            String username
    ) {

        return ResponseEntity.ok(

                activityLogService.filterLogs(
                        module,
                        action,
                        username
                )
        );
    }
}