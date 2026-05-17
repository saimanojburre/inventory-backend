package com.inventory.system.inventory.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.inventory.system.inventory.dto.InventoryResponse;
import com.inventory.system.inventory.dto.PurchaseSummary;
import com.inventory.system.inventory.dto.UsageSummary;
import com.inventory.system.item.entity.Item;
import com.inventory.system.item.repository.ItemRepository;
import com.inventory.system.purchase.repository.PurchaseRepository;
import com.inventory.system.usage.repository.UsageRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class InventoryService {

    private final ItemRepository itemRepository;

    private final PurchaseRepository purchaseRepository;

    private final UsageRepository usageRepository;

    public InventoryService(
            ItemRepository itemRepository,
            PurchaseRepository purchaseRepository,
            UsageRepository usageRepository
    ) {

        this.itemRepository = itemRepository;
        this.purchaseRepository = purchaseRepository;
        this.usageRepository = usageRepository;
    }

    public List<InventoryResponse> getInventory() {

        List<Item> items =
                itemRepository.findByActiveTrue();

        List<PurchaseSummary> purchaseSummaries =
                purchaseRepository.getPurchaseSummary();

        List<UsageSummary> usageSummaries =
                usageRepository.getUsageSummary();

        Map<Long, PurchaseSummary> purchaseMap =
                purchaseSummaries.stream()
                        .collect(Collectors.toMap(
                                PurchaseSummary::getItemId,
                                purchase -> purchase
                        ));

        Map<Long, UsageSummary> usageMap =
                usageSummaries.stream()
                        .collect(Collectors.toMap(
                                UsageSummary::getItemId,
                                usage -> usage
                        ));

        List<InventoryResponse> response =
                new ArrayList<>();

        for (Item item : items) {

            response.add(
                    mapInventoryResponse(
                            item,
                            purchaseMap.get(item.getId()),
                            usageMap.get(item.getId())
                    )
            );
        }

        return response;
    }

    public BigDecimal getAverageCost(
            Long itemId
    ) {

        BigDecimal avgPrice =
                purchaseRepository.getAveragePrice(itemId);

        return avgPrice != null
                ? avgPrice
                : BigDecimal.ZERO;
    }

    // ================= PRIVATE METHODS =================

    private InventoryResponse mapInventoryResponse(
            Item item,
            PurchaseSummary purchase,
            UsageSummary usage
    ) {

        BigDecimal purchased =
                purchase != null
                        ? purchase.getTotalPurchased()
                        : BigDecimal.ZERO;

        BigDecimal avgPrice =
                purchase != null
                        ? purchase.getAvgPrice()
                        : BigDecimal.ZERO;

        BigDecimal used =
                usage != null
                        ? usage.getTotalUsed()
                        : BigDecimal.ZERO;

        BigDecimal quantity =
                purchased.subtract(used);

        BigDecimal totalValue =
                quantity.multiply(avgPrice);

        InventoryResponse response =
                new InventoryResponse();

        response.setItemId(item.getId());

        response.setCategory(
                item.getCategory()
        );

        response.setItemName(
                item.getName()
        );

        response.setMinStock(
                item.getMinStock()
        );

        response.setUnits(
                item.getUnit()
        );

        response.setQuantity(quantity);

        response.setCost(avgPrice);

        response.setTotal(totalValue);

        return response;
    }
}