package com.inventory.system.usage.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.inventory.system.exception.BadRequestException;
import com.inventory.system.exception.ResourceNotFoundException;
import com.inventory.system.inventory.service.InventoryService;
import com.inventory.system.item.entity.Item;
import com.inventory.system.item.repository.ItemRepository;
import com.inventory.system.purchase.repository.PurchaseRepository;
import com.inventory.system.usage.entity.Usage;
import com.inventory.system.usage.repository.UsageRepository;

@Service
public class UsageService {

	private final UsageRepository usageRepository;
	private final ItemRepository itemRepository;
	private final PurchaseRepository purchaseRepository;
	private final InventoryService inventoryService;

	public UsageService(UsageRepository usageRepository, ItemRepository itemRepository,
			PurchaseRepository purchaseRepository, InventoryService inventoryService) {

		this.usageRepository = usageRepository;
		this.itemRepository = itemRepository;
		this.purchaseRepository = purchaseRepository;
		this.inventoryService = inventoryService;
	}

	// ================= CREATE USAGE =================

	public Usage createUsage(Usage usage) {

		Long itemId = usage.getItem().getId();

		Item item = itemRepository.findById(itemId).orElseThrow(() -> new ResourceNotFoundException("Item not found"));

		Double purchased = purchaseRepository.getTotalPurchased(itemId);
		Double used = usageRepository.getTotalUsed(itemId);

		if (purchased == null)
			purchased = 0.0;
		if (used == null)
			used = 0.0;

		Double currentStock = purchased - used;

		if (usage.getQuantity() > currentStock) {
			throw new BadRequestException("Not enough stock. Available stock: " + currentStock);
		}

		// 🔥 GET AVG COST FROM INVENTORY
		Double avgCost = inventoryService.getAverageCost(itemId);

		if (avgCost == null)
			avgCost = 0.0;

		// 🔥 STORE COST (IMPORTANT)
		usage.setCostPerUnit(avgCost);
		usage.setTotalCost(avgCost * usage.getQuantity());

		usage.setItem(item);

		return usageRepository.save(usage);
	}

	// ================= BULK SAVE =================

	public List<Usage> createUsages(List<Usage> usages) {

		for (Usage usage : usages) {

			Long itemId = usage.getItem().getId();

			Double purchased = purchaseRepository.getTotalPurchased(itemId);
			Double used = usageRepository.getTotalUsed(itemId);

			if (purchased == null)
				purchased = 0.0;
			if (used == null)
				used = 0.0;

			Double currentStock = purchased - used;

			if (usage.getQuantity() > currentStock) {
				throw new BadRequestException("Not enough stock for item id: " + itemId);
			}

			// 🔥 APPLY COST FOR EACH
			Double avgCost = inventoryService.getAverageCost(itemId);

			if (avgCost == null)
				avgCost = 0.0;

			usage.setCostPerUnit(avgCost);
			usage.setTotalCost(avgCost * usage.getQuantity());
		}

		return usageRepository.saveAll(usages);
	}

	// ================= GET =================

	public List<Usage> getAllUsage() {
		return usageRepository.findAll();
	}

	public Usage getUsage(Long id) {
		return usageRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Usage not found"));
	}

	// ================= UPDATE =================

	public Usage updateUsage(Long id, Usage usage) {

		Usage existing = usageRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Usage not found"));

		existing.setQuantity(usage.getQuantity());
		existing.setDepartment(usage.getDepartment());
		existing.setTakenBy(usage.getTakenBy());
		existing.setGivenBy(usage.getGivenBy());
		existing.setUsedDateTime(usage.getUsedDateTime());

		// 🔥 Recalculate cost on update
		Double avgCost = inventoryService.getAverageCost(existing.getItem().getId());

		if (avgCost == null)
			avgCost = 0.0;

		existing.setCostPerUnit(avgCost);
		existing.setTotalCost(avgCost * existing.getQuantity());

		return usageRepository.save(existing);
	}

	// ================= DELETE =================

	public void deleteUsage(Long id) {

		Usage usage = usageRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Usage not found"));

		usageRepository.delete(usage);
	}

	// ================= REPORT =================

	public List<Map<String, Object>> getUsageReport(String fromDate, String toDate) {

		List<Usage> usages;

		if (fromDate != null && toDate != null) {
			LocalDateTime from = LocalDate.parse(fromDate).atStartOfDay();
			LocalDateTime to = LocalDate.parse(toDate).atTime(23, 59, 59);

			usages = usageRepository.findByUsedDateTimeBetween(from, to);
		} else {
			usages = usageRepository.findAll();
		}

		List<String> categories = List.of("Raw Materials", "Packing Materials", "Chicken", "Mutton", "Fish & Prawns",
				"Butter, Cheese, Cream", "Cool Drinks & Water Bottles", "Sanitary");

		Map<String, Map<String, Object>> result = new LinkedHashMap<>();

		for (Usage u : usages) {

			String dept = u.getDepartment();
			String category = u.getItem().getCategory();

			// ✅ USE STORED COST
			double cost = calculateCost(u);

			result.putIfAbsent(dept, createEmptyRow(dept, categories));

			Map<String, Object> row = result.get(dept);

			row.put(category, (double) row.get(category) + cost);
			row.put("total", (double) row.get("total") + cost);
		}

		// TOTAL ROW
		Map<String, Object> totalRow = createEmptyRow("Total", categories);

		for (Map<String, Object> row : result.values()) {

			for (String cat : categories) {
				totalRow.put(cat, (double) totalRow.get(cat) + (double) row.get(cat));
			}

			totalRow.put("total", (double) totalRow.get("total") + (double) row.get("total"));
		}

		List<Map<String, Object>> finalResult = new ArrayList<>(result.values());
		finalResult.add(totalRow);

		return finalResult;
	}

	private Map<String, Object> createEmptyRow(String dept, List<String> categories) {

		Map<String, Object> row = new LinkedHashMap<>();
		row.put("department", dept);
		row.put("total", 0.0);

		for (String c : categories) {
			row.put(c, 0.0);
		}

		return row;
	}

	private double calculateCost(Usage u) {

		// ✅ if cost exists → use it
		if (u.getTotalCost() != null) {
			return u.getTotalCost();
		}

		// 🔥 fallback (temporary fix)
		Double avgCost = inventoryService.getAverageCost(u.getItem().getId());

		if (avgCost != null && u.getQuantity() != null) {
			return avgCost * u.getQuantity();
		}

		return 0.0;
	}
}