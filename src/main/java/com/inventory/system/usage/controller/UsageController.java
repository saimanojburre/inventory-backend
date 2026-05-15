package com.inventory.system.usage.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.inventory.system.usage.entity.Usage;
import com.inventory.system.usage.service.UsageService;

@RestController
@RequestMapping("/usage")
public class UsageController {

    private final UsageService usageService;

    public UsageController(UsageService usageService) {
        this.usageService = usageService;
    }

    @PostMapping
    public ResponseEntity<Usage> createUsage(
            @RequestBody Usage usage
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(usageService.createUsage(usage));
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<Usage>> createUsages(
            @RequestBody List<Usage> usages
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(usageService.createUsages(usages));
    }

    @GetMapping
    public ResponseEntity<List<Usage>> getUsage() {

        return ResponseEntity.ok(
                usageService.getAllUsage()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usage> getUsage(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                usageService.getUsage(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usage> updateUsage(
            @PathVariable Long id,
            @RequestBody Usage usage
    ) {

        return ResponseEntity.ok(
                usageService.updateUsage(id, usage)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteUsage(
            @PathVariable Long id
    ) {

        usageService.deleteUsage(id);

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Usage deleted successfully"
                )
        );
    }

    @GetMapping("/report")
    public ResponseEntity<List<Map<String, Object>>> getUsageReport(
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate
    ) {

        return ResponseEntity.ok(
                usageService.getUsageReport(
                        fromDate,
                        toDate
                )
        );
    }
}