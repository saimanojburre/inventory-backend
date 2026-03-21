package com.inventory.system.purchase.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.inventory.system.purchase.entity.Purchase;
import com.inventory.system.purchase.service.PurchaseService;

@RestController
@RequestMapping("/purchases")
public class PurchaseController {

	private final PurchaseService purchaseService;

	public PurchaseController(PurchaseService purchaseService) {
		this.purchaseService = purchaseService;
	}

	// CREATE PURCHASE
	@PostMapping
	public Purchase createPurchase(@RequestBody Purchase purchase) {
		return purchaseService.createPurchase(purchase);
	}

	// BULK PURCHASE
	@PostMapping("/bulk")
	public List<Purchase> createPurchases(@RequestBody List<Purchase> purchases) {
		return purchaseService.createPurchases(purchases);
	}

	// GET ALL PURCHASES
	@GetMapping
	public List<Purchase> getPurchases() {
		return purchaseService.getAllPurchases();
	}

	// GET PURCHASE BY ID
	@GetMapping("/{id}")
	public Purchase getPurchase(@PathVariable Long id) {
		return purchaseService.getPurchase(id);
	}

	// UPDATE PURCHASE
	@PutMapping("/{id}")
	public Purchase updatePurchase(@PathVariable Long id, @RequestBody Purchase purchase) {

		return purchaseService.updatePurchase(id, purchase);
	}

	// DELETE PURCHASE
	@DeleteMapping("/{id}")
	public String deletePurchase(@PathVariable Long id) {

		purchaseService.deletePurchase(id);

		return "Purchase deleted successfully";
	}
}