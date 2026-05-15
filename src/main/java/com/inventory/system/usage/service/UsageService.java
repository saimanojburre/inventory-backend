package com.inventory.system.usage.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventory.system.exception.BadRequestException;
import com.inventory.system.exception.ResourceNotFoundException;
import com.inventory.system.inventory.service.InventoryService;
import com.inventory.system.item.entity.Item;
import com.inventory.system.item.repository.ItemRepository;
import com.inventory.system.purchase.repository.PurchaseRepository;
import com.inventory.system.usage.entity.Usage;
import com.inventory.system.usage.repository.UsageRepository;

@Service
@Transactional
public class UsageService {

    private final UsageRepository usageRepository;
    private final ItemRepository itemRepository;
    private final PurchaseRepository purchaseRepository;
    private final InventoryService inventoryService;

    public UsageService(
            UsageRepository usageRepository,
            ItemRepository itemRepository,
            PurchaseRepository purchaseRepository,
            InventoryService inventoryService
    ) {
        this.usageRepository = usageRepository;
        this.itemRepository = itemRepository;
        this.purchaseRepository = purchaseRepository;
        this.inventoryService = inventoryService;
    }

    // ================= CREATE USAGE =================

    public Usage createUsage(Usage usage) {

        Item item = validateAndGetItem(
                usage.getItem().getId()
        );

        validateStock(
                item.getId(),
                usage.getQuantity()
        );

        applyCosting(
                usage,
                item.getId()
        );

        usage.setItem(item);

        return usageRepository.save(usage);
    }

    // ================= BULK SAVE =================

    public List<Usage> createUsages(List<Usage> usages) {

        for (Usage usage : usages) {

            Long itemId =
                    usage.getItem().getId();

            Item item =
                    validateAndGetItem(itemId);

            validateStock(
                    itemId,
                    usage.getQuantity()
            );

            applyCosting(
                    usage,
                    itemId
            );

            usage.setItem(item);
        }

        return usageRepository.saveAll(usages);
    }

    // ================= GET =================

    @Transactional(readOnly = true)
    public List<Usage> getAllUsage() {

        return usageRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Usage getUsage(Long id) {

        return usageRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Usage not found"
                        )
                );
    }

    // ================= UPDATE =================

    public Usage updateUsage(
            Long id,
            Usage usage
    ) {

        Usage existing =
                usageRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Usage not found"
                                )
                        );

        validateStock(
                existing.getItem().getId(),
                usage.getQuantity()
        );

        existing.setQuantity(
                usage.getQuantity()
        );

        existing.setDepartment(
                usage.getDepartment()
        );

        existing.setTakenBy(
                usage.getTakenBy()
        );

        existing.setGivenBy(
                usage.getGivenBy()
        );

        existing.setUsedDateTime(
                usage.getUsedDateTime()
        );

        applyCosting(
                existing,
                existing.getItem().getId()
        );

        return usageRepository.save(existing);
    }

    // ================= DELETE =================

    public void deleteUsage(Long id) {

        if (!usageRepository.existsById(id)) {

            throw new ResourceNotFoundException(
                    "Usage not found"
            );
        }

        usageRepository.deleteById(id);
    }

    // ================= REPORT =================

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getUsageReport(
            String fromDate,
            String toDate
    ) {

        List<Usage> usages;

        if (fromDate != null && toDate != null) {

            LocalDateTime from =
                    LocalDate.parse(fromDate)
                            .atStartOfDay();

            LocalDateTime to =
                    LocalDate.parse(toDate)
                            .atTime(23, 59, 59);

            usages = usageRepository
                    .findByUsedDateTimeBetween(
                            from,
                            to
                    );

        } else {

            usages = usageRepository.findAll();
        }

        List<String> categories = List.of(
                "Raw Materials",
                "Packing Materials",
                "Chicken",
                "Mutton",
                "Fish & Prawns",
                "Butter, Cheese, Cream",
                "Cool Drinks & Water Bottles",
                "Sanitary"
        );

        Map<String, Map<String, Object>> result =
                new LinkedHashMap<>();

        for (Usage usage : usages) {

            String department =
                    usage.getDepartment();

            String category =
                    usage.getItem().getCategory();

            BigDecimal cost =
                    calculateCost(usage);

            result.putIfAbsent(
                    department,
                    createEmptyRow(
                            department,
                            categories
                    )
            );

            Map<String, Object> row =
                    result.get(department);

            row.put(
                    category,
                    ((BigDecimal) row.get(category))
                            .add(cost)
            );

            row.put(
                    "total",
                    ((BigDecimal) row.get("total"))
                            .add(cost)
            );
        }

        // ================= TOTAL ROW =================

        Map<String, Object> totalRow =
                createEmptyRow(
                        "Total",
                        categories
                );

        for (Map<String, Object> row : result.values()) {

            for (String category : categories) {

                totalRow.put(
                        category,
                        ((BigDecimal) totalRow.get(category))
                                .add(
                                        (BigDecimal) row.get(category)
                                )
                );
            }

            totalRow.put(
                    "total",
                    ((BigDecimal) totalRow.get("total"))
                            .add(
                                    (BigDecimal) row.get("total")
                            )
            );
        }

        List<Map<String, Object>> finalResult =
                new ArrayList<>(result.values());

        finalResult.add(totalRow);

        return finalResult;
    }

    // ================= PRIVATE METHODS =================

    private Item validateAndGetItem(
            Long itemId
    ) {

        return itemRepository.findById(itemId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Item not found"
                        )
                );
    }

    private void validateStock(
            Long itemId,
            BigDecimal requestedQuantity
    ) {

        BigDecimal purchased =
                purchaseRepository.getTotalPurchased(itemId);

        BigDecimal used =
                usageRepository.getTotalUsed(itemId);

        purchased = purchased != null
                ? purchased
                : BigDecimal.ZERO;

        used = used != null
                ? used
                : BigDecimal.ZERO;

        BigDecimal currentStock =
                purchased.subtract(used);

        if (requestedQuantity.compareTo(currentStock) > 0) {

            throw new BadRequestException(
                    "Not enough stock. Available stock: "
                            + currentStock
            );
        }
    }

    private void applyCosting(
            Usage usage,
            Long itemId
    ) {

        BigDecimal avgCost =
                inventoryService.getAverageCost(itemId);

        avgCost = avgCost != null
                ? avgCost
                : BigDecimal.ZERO;

        usage.setCostPerUnit(avgCost);

        usage.setTotalCost(
                avgCost.multiply(
                        usage.getQuantity()
                )
        );
    }

    private Map<String, Object> createEmptyRow(
            String department,
            List<String> categories
    ) {

        Map<String, Object> row =
                new LinkedHashMap<>();

        row.put("department", department);

        row.put("total", BigDecimal.ZERO);

        for (String category : categories) {

            row.put(
                    category,
                    BigDecimal.ZERO
            );
        }

        return row;
    }

    private BigDecimal calculateCost(
            Usage usage
    ) {

        if (usage.getTotalCost() != null) {

            return usage.getTotalCost();
        }

        BigDecimal avgCost =
                inventoryService.getAverageCost(
                        usage.getItem().getId()
                );

        if (avgCost != null &&
                usage.getQuantity() != null) {

            return avgCost.multiply(
                    usage.getQuantity()
            );
        }

        return BigDecimal.ZERO;
    }
}