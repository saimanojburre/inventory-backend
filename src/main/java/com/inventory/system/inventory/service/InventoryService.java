package com.inventory.system.inventory.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.inventory.system.inventory.dto.InventoryResponse;
import com.inventory.system.item.entity.Item;
import com.inventory.system.item.repository.ItemRepository;
import com.inventory.system.purchase.repository.PurchaseRepository;
import com.inventory.system.usage.repository.UsageRepository;

@Service
public class InventoryService {

	private final ItemRepository itemRepository;
	private final PurchaseRepository purchaseRepository;
	private final UsageRepository usageRepository;

	public InventoryService(ItemRepository itemRepository, PurchaseRepository purchaseRepository,
			UsageRepository usageRepository) {

		this.itemRepository = itemRepository;
		this.purchaseRepository = purchaseRepository;
		this.usageRepository = usageRepository;
	}

	public List<InventoryResponse> getInventory() {

		List<Item> items = itemRepository.findByActiveTrue();
		List<InventoryResponse> response = new ArrayList<>();

		for (Item item : items) {

			Double purchased = purchaseRepository.getTotalPurchased(item.getId());
			Double used = usageRepository.getTotalUsed(item.getId());
			Double avgPrice = purchaseRepository.getAveragePrice(item.getId());

			if (purchased == null)
				purchased = 0.0;

			if (used == null)
				used = 0.0;

			if (avgPrice == null)
				avgPrice = 0.0;

			Double quantity = purchased - used;
			Double totalValue = quantity * avgPrice;

			InventoryResponse inv = new InventoryResponse();

			inv.setItemId(item.getId());
			inv.setCategory(item.getCategory());
			inv.setItemName(item.getName());
			inv.setMinStock(item.getMinStock());
			inv.setUnits(item.getUnit());
			inv.setQuantity(quantity);
			inv.setCost(avgPrice);
			inv.setTotal(totalValue);

			response.add(inv);
		}

		return response;
	}

	public Double getAverageCost(Long itemId) {

		Double avgPrice = purchaseRepository.getAveragePrice(itemId);

		if (avgPrice == null) {
			return 0.0;
		}

		return avgPrice;
	}
}