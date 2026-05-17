package com.inventory.system.usage.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventory.system.activity.enums.ActionType;
import com.inventory.system.activity.enums.ModuleType;
import com.inventory.system.activity.service.ActivityLogService;
import com.inventory.system.exception.BadRequestException;
import com.inventory.system.exception.ResourceNotFoundException;
import com.inventory.system.inventory.service.InventoryService;
import com.inventory.system.item.entity.Item;
import com.inventory.system.item.repository.ItemRepository;
import com.inventory.system.purchase.repository.PurchaseRepository;
import com.inventory.system.usage.entity.Usage;
import com.inventory.system.usage.repository.UsageRepository;
import com.inventory.system.user.entity.User;

@Service
@Transactional
public class UsageService {

    private final UsageRepository usageRepository;

    private final ItemRepository itemRepository;

    private final PurchaseRepository purchaseRepository;

    private final InventoryService inventoryService;

    private final ActivityLogService activityLogService;

    public UsageService(
            UsageRepository usageRepository,
            ItemRepository itemRepository,
            PurchaseRepository purchaseRepository,
            InventoryService inventoryService,
            ActivityLogService activityLogService
    ) {

        this.usageRepository = usageRepository;

        this.itemRepository = itemRepository;

        this.purchaseRepository = purchaseRepository;

        this.inventoryService = inventoryService;

        this.activityLogService = activityLogService;
    }

    // =====================================================
    // CREATE USAGE
    // =====================================================

    public Usage createUsage(
            Usage usage,
            String sessionId
    ) {

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

        Usage savedUsage =
                usageRepository.save(usage);

        User currentUser =
                getCurrentUser();

        activityLogService.log(

                currentUser.getId(),

                currentUser.getUsername(),

                currentUser.getRole().getName(),

                ModuleType.USAGE,

                ActionType.CREATE,

                buildCreateDescription(savedUsage),

                savedUsage.getId(),

                savedUsage.getItem().getName(),

                sessionId,

                "SUCCESS"
        );

        return savedUsage;
    }

    // =====================================================
    // BULK CREATE
    // =====================================================

    public List<Usage> createUsages(
            List<Usage> usages,
            String sessionId
    ) {

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

        List<Usage> savedUsages =
                usageRepository.saveAll(usages);

        User currentUser =
                getCurrentUser();

        activityLogService.log(

                currentUser.getId(),

                currentUser.getUsername(),

                currentUser.getRole().getName(),

                ModuleType.USAGE,

                ActionType.CREATE,

                "Created bulk usage with "
                        + savedUsages.size()
                        + " items",

                null,

                "Bulk Usage",

                sessionId,

                "SUCCESS"
        );

        return savedUsages;
    }

    // =====================================================
    // GET ALL
    // =====================================================

    @Transactional(readOnly = true)
    public List<Usage> getAllUsage() {

        return usageRepository.findAll();
    }

    // =====================================================
    // GET BY ID
    // =====================================================

    @Transactional(readOnly = true)
    public Usage getUsage(
            Long id
    ) {

        return usageRepository.findById(id)
                .orElseThrow(() ->

                        new ResourceNotFoundException(
                                "Usage not found"
                        )
                );
    }

    // =====================================================
    // UPDATE
    // =====================================================

    public Usage updateUsage(

            Long id,

            Usage usage,

            String sessionId
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

        Usage updatedUsage =
                usageRepository.save(existing);

        User currentUser =
                getCurrentUser();

        activityLogService.log(

                currentUser.getId(),

                currentUser.getUsername(),

                currentUser.getRole().getName(),

                ModuleType.USAGE,

                ActionType.UPDATE,

                buildUpdateDescription(updatedUsage),

                updatedUsage.getId(),

                updatedUsage.getItem().getName(),

                sessionId,

                "SUCCESS"
        );

        return updatedUsage;
    }

    // =====================================================
    // DELETE
    // =====================================================

    public void deleteUsage(

            Long id,

            String sessionId
    ) {

        Usage usage =
                usageRepository.findById(id)
                        .orElseThrow(() ->

                                new ResourceNotFoundException(
                                        "Usage not found"
                                )
                        );

        usageRepository.deleteById(id);

        User currentUser =
                getCurrentUser();

        activityLogService.log(

                currentUser.getId(),

                currentUser.getUsername(),

                currentUser.getRole().getName(),

                ModuleType.USAGE,

                ActionType.DELETE,

                buildDeleteDescription(usage),

                usage.getId(),

                usage.getItem().getName(),

                sessionId,

                "SUCCESS"
        );
    }

    // =====================================================
    // REPORT
    // =====================================================

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getUsageReport(
            String fromDate,
            String toDate
    ) {

        List<Usage> usages;

        if (fromDate != null &&
                toDate != null) {

            LocalDateTime from =
                    LocalDate.parse(fromDate)
                            .atStartOfDay();

            LocalDateTime to =
                    LocalDate.parse(toDate)
                            .atTime(23, 59, 59);

            usages =
                    usageRepository.findByUsedDateTimeBetween(
                            from,
                            to
                    );

        } else {

            usages = usageRepository.findAll();
        }

        List<String> categories =
                List.of(
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

    // =====================================================
    // PRIVATE METHODS
    // =====================================================

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

    // =====================================================
    // CURRENT USER
    // =====================================================

    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        return (User) authentication.getPrincipal();
    }

    // =====================================================
    // DESCRIPTIONS
    // =====================================================

    private String buildCreateDescription(
            Usage usage
    ) {

        return "Created usage for item "
                + usage.getItem().getName()
                + " quantity "
                + usage.getQuantity();
    }

    private String buildUpdateDescription(
            Usage usage
    ) {

        return "Updated usage for item "
                + usage.getItem().getName()
                + " quantity "
                + usage.getQuantity();
    }

    private String buildDeleteDescription(
            Usage usage
    ) {

        return "Deleted usage for item "
                + usage.getItem().getName()
                + " quantity "
                + usage.getQuantity();
    }
}