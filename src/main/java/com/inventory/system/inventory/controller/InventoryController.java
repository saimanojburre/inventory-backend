package com.inventory.system.inventory.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.inventory.system.inventory.dto.InventoryResponse;
import com.inventory.system.inventory.service.InventoryService;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

	private final InventoryService inventoryService;

	public InventoryController(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	@GetMapping
	public List<InventoryResponse> getInventory() {

		return inventoryService.getInventory();
	}
}