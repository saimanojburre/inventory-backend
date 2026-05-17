package com.inventory.system.usage.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inventory.system.usage.entity.Usage;
import com.inventory.system.usage.service.UsageService;

@RestController
@RequestMapping("/usage")
public class UsageController {

    private final UsageService usageService;

    public UsageController(
            UsageService usageService
    ) {

        this.usageService = usageService;
    }

    // =====================================================
    // CREATE USAGE
    // =====================================================

    @PostMapping
    public ResponseEntity<Usage> createUsage(

            @RequestBody Usage usage,

            @RequestHeader(
                    value = "X-Session-Id",
                    required = false
            )
            String sessionId
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        usageService.createUsage(
                                usage,
                                sessionId
                        )
                );
    }

    // =====================================================
    // BULK CREATE USAGE
    // =====================================================

    @PostMapping("/bulk")
    public ResponseEntity<List<Usage>> createUsages(

            @RequestBody List<Usage> usages,

            @RequestHeader(
                    value = "X-Session-Id",
                    required = false
            )
            String sessionId
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        usageService.createUsages(
                                usages,
                                sessionId
                        )
                );
    }

    // =====================================================
    // GET ALL
    // =====================================================

    @GetMapping
    public ResponseEntity<List<Usage>> getUsage() {

        return ResponseEntity.ok(
                usageService.getAllUsage()
        );
    }

    // =====================================================
    // GET BY ID
    // =====================================================

    @GetMapping("/{id}")
    public ResponseEntity<Usage> getUsage(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                usageService.getUsage(id)
        );
    }

    // =====================================================
    // UPDATE
    // =====================================================

    @PutMapping("/{id}")
    public ResponseEntity<Usage> updateUsage(

            @PathVariable Long id,

            @RequestBody Usage usage,

            @RequestHeader(
                    value = "X-Session-Id",
                    required = false
            )
            String sessionId
    ) {

        return ResponseEntity.ok(
                usageService.updateUsage(
                        id,
                        usage,
                        sessionId
                )
        );
    }

    // =====================================================
    // DELETE
    // =====================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteUsage(

            @PathVariable Long id,

            @RequestHeader(
                    value = "X-Session-Id",
                    required = false
            )
            String sessionId
    ) {

        usageService.deleteUsage(
                id,
                sessionId
        );

        return ResponseEntity.ok(

                Map.of(
                        "message",
                        "Usage deleted successfully"
                )
        );
    }

    // =====================================================
    // REPORT
    // =====================================================

    @GetMapping("/report")
    public ResponseEntity<List<Map<String, Object>>> getUsageReport(

            @RequestParam(required = false)
            String fromDate,

            @RequestParam(required = false)
            String toDate
    ) {

        return ResponseEntity.ok(

                usageService.getUsageReport(
                        fromDate,
                        toDate
                )
        );
    }
}