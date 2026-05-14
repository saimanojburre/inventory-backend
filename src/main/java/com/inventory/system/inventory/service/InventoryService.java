package com.inventory.system.inventory.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.inventory.system.inventory.dto.PurchaseSummary;
import com.inventory.system.inventory.dto.UsageSummary;
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

        List<PurchaseSummary> purchaseSummaries =
                purchaseRepository.getPurchaseSummary();

        List<UsageSummary> usageSummaries =
                usageRepository.getUsageSummary();

        Map<Long, PurchaseSummary> purchaseMap =
                purchaseSummaries.stream()
                        .collect(Collectors.toMap(
                                PurchaseSummary::getItemId,
                                p -> p
                        ));

        Map<Long, UsageSummary> usageMap =
                usageSummaries.stream()
                        .collect(Collectors.toMap(
                                UsageSummary::getItemId,
                                u -> u
                        ));

        List<InventoryResponse> response = new ArrayList<>();

        for (Item item : items) {

            PurchaseSummary purchase =
                    purchaseMap.get(item.getId());

            UsageSummary usage =
                    usageMap.get(item.getId());

            double purchased =
                    purchase != null
                            ? purchase.getTotalPurchased()
                            : 0.0;

            double avgPrice =
                    purchase != null
                            ? purchase.getAvgPrice()
                            : 0.0;

            double used =
                    usage != null
                            ? usage.getTotalUsed()
                            : 0.0;

            double quantity = purchased - used;

            double totalValue = quantity * avgPrice;

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