package com.inventory.system.item.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.inventory.system.item.dto.BulkItemResponse;
import com.inventory.system.item.entity.Item;
import com.inventory.system.item.service.ItemService;

@RestController
@RequestMapping("/items")
public class ItemController {

	private final ItemService itemService;

	public ItemController(ItemService itemService) {
		this.itemService = itemService;
	}

	// CREATE ITEM
	@PostMapping
	public Item createItem(@RequestBody Item item) {
		return itemService.createItem(item);
	}

	// BULK SAVE
	@PostMapping("/bulk")
	public BulkItemResponse createItems(@RequestBody List<Item> items) {
		return itemService.createItems(items);
	}

	// GET ALL ITEMS
	@GetMapping
	public ResponseEntity<?> getItems() {
		try {
			return ResponseEntity.ok(itemService.getAllItems());
		} catch (Exception e) {
			e.printStackTrace(); // 🔥 NOW logs will show
			return ResponseEntity.status(500).body(e.toString());
		}
	}

	// GET ITEM BY ID
	@GetMapping("/{id}")
	public Item getItem(@PathVariable Long id) {
		return itemService.getItem(id);
	}

	// UPDATE ITEM
	@PutMapping("/{id}")
	public Item updateItem(@PathVariable Long id, @RequestBody Item item) {

		return itemService.updateItem(id, item);
	}

	// DELETE ITEM
	@DeleteMapping("/{id}")
	public Map<String, String> deleteItem(@PathVariable Long id) {

		itemService.deleteItem(id);

		return Map.of("message", "Item deleted successfully");
	}
}