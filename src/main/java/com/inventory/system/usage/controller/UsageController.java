package com.inventory.system.usage.controller;

import java.util.List;
import java.util.Map;

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
	public Usage createUsage(@RequestBody Usage usage) {
		return usageService.createUsage(usage);
	}

	@PostMapping("/bulk")
	public List<Usage> createUsages(@RequestBody List<Usage> usages) {

		return usageService.createUsages(usages);
	}

	@GetMapping
	public List<Usage> getUsage() {
		return usageService.getAllUsage();
	}

	@GetMapping("/{id}")
	public Usage getUsage(@PathVariable Long id) {
		return usageService.getUsage(id);
	}

	@PutMapping("/{id}")
	public Usage updateUsage(@PathVariable Long id, @RequestBody Usage usage) {

		return usageService.updateUsage(id, usage);
	}

	@DeleteMapping("/{id}")
	public String deleteUsage(@PathVariable Long id) {

		usageService.deleteUsage(id);

		return "Usage deleted";
	}

	@GetMapping("/report")
	public ResponseEntity<List<Map<String, Object>>> getUsageReport(@RequestParam(required = false) String fromDate,
			@RequestParam(required = false) String toDate) {

		return ResponseEntity.ok(usageService.getUsageReport(fromDate, toDate));
	}
}