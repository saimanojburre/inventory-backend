package com.inventory.system.purchase.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventory.system.activity.enums.ActionType;
import com.inventory.system.activity.enums.ModuleType;
import com.inventory.system.activity.service.ActivityLogService;
import com.inventory.system.exception.ResourceNotFoundException;
import com.inventory.system.item.entity.Item;
import com.inventory.system.item.repository.ItemRepository;
import com.inventory.system.purchase.dto.PurchaseResponseDto;
import com.inventory.system.purchase.entity.Purchase;
import com.inventory.system.purchase.repository.PurchaseRepository;
import com.inventory.system.user.entity.User;

@Service
@Transactional
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;

    private final ItemRepository itemRepository;

    private final ActivityLogService activityLogService;

    public PurchaseService(
            PurchaseRepository purchaseRepository,
            ItemRepository itemRepository,
            ActivityLogService activityLogService
    ) {

        this.purchaseRepository = purchaseRepository;

        this.itemRepository = itemRepository;

        this.activityLogService = activityLogService;
    }

    // =====================================================
    // CREATE PURCHASE
    // =====================================================

    public Purchase createPurchase(
            Purchase purchase,
            String sessionId
    ) {

        Long itemId = purchase.getItem().getId();

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() ->

                        new ResourceNotFoundException(
                                "Item not found"
                        )
                );

        purchase.setItem(item);

        Purchase savedPurchase =
                purchaseRepository.save(purchase);

        User currentUser = getCurrentUser();

        activityLogService.log(

                currentUser.getId(),

                currentUser.getUsername(),

                currentUser.getRole().getName(),

                ModuleType.PURCHASE,

                ActionType.CREATE,

                buildCreateDescription(savedPurchase),

                savedPurchase.getId(),

                item.getName(),

                sessionId,

                "SUCCESS"
        );

        return savedPurchase;
    }

    // =====================================================
    // BULK PURCHASE
    // =====================================================

    public List<Purchase> createPurchases(
            List<Purchase> purchases,
            String sessionId
    ) {

        // COLLECT ITEM IDS

        Set<Long> itemIds = purchases.stream()
                .map(purchase -> purchase.getItem().getId())
                .collect(Collectors.toSet());

        // FETCH ITEMS

        Map<Long, Item> itemMap =
                itemRepository.findAllById(itemIds)
                        .stream()
                        .collect(Collectors.toMap(
                                Item::getId,
                                item -> item
                        ));

        // VALIDATE ITEMS

        for (Purchase purchase : purchases) {

            Long itemId = purchase.getItem().getId();

            Item item = itemMap.get(itemId);

            if (item == null) {

                throw new ResourceNotFoundException(
                        "Item not found: " + itemId
                );
            }

            purchase.setItem(item);
        }

        List<Purchase> savedPurchases =
                purchaseRepository.saveAll(purchases);

        User currentUser = getCurrentUser();

        activityLogService.log(

                currentUser.getId(),

                currentUser.getUsername(),

                currentUser.getRole().getName(),

                ModuleType.PURCHASE,

                ActionType.CREATE,

                "Created bulk purchase with "
                        + savedPurchases.size()
                        + " items",

                null,

                "Bulk Purchase",

                sessionId,

                "SUCCESS"
        );

        return savedPurchases;
    }

    // =====================================================
    // GET ALL PURCHASES
    // =====================================================

    @Transactional(readOnly = true)
    public List<PurchaseResponseDto> getAllPurchases() {

        return purchaseRepository.getAllPurchaseDtos();
    }

    // =====================================================
    // GET PURCHASE BY ID
    // =====================================================

    @Transactional(readOnly = true)
    public Purchase getPurchase(
            Long id
    ) {

        return purchaseRepository.findById(id)
                .orElseThrow(() ->

                        new ResourceNotFoundException(
                                "Purchase not found"
                        )
                );
    }

    // =====================================================
    // UPDATE PURCHASE
    // =====================================================

    public Purchase updatePurchase(

            Long id,

            Purchase purchase,

            String sessionId
    ) {

        Purchase existing =
                purchaseRepository.findById(id)
                        .orElseThrow(() ->

                                new ResourceNotFoundException(
                                        "Purchase not found"
                                )
                        );

        existing.setQuantity(
                purchase.getQuantity()
        );

        existing.setPrice(
                purchase.getPrice()
        );

        existing.setSupplier(
                purchase.getSupplier()
        );

        existing.setPurchaseDate(
                purchase.getPurchaseDate()
        );

        Purchase updatedPurchase =
                purchaseRepository.save(existing);

        User currentUser = getCurrentUser();

        activityLogService.log(

                currentUser.getId(),

                currentUser.getUsername(),

                currentUser.getRole().getName(),

                ModuleType.PURCHASE,

                ActionType.UPDATE,

                buildUpdateDescription(updatedPurchase),

                updatedPurchase.getId(),

                updatedPurchase.getItem().getName(),

                sessionId,

                "SUCCESS"
        );

        return updatedPurchase;
    }

    // =====================================================
    // DELETE PURCHASE
    // =====================================================

    public void deletePurchase(

            Long id,

            String sessionId
    ) {

        Purchase purchase =
                purchaseRepository.findById(id)
                        .orElseThrow(() ->

                                new ResourceNotFoundException(
                                        "Purchase not found"
                                )
                        );

        purchaseRepository.deleteById(id);

        User currentUser = getCurrentUser();

        activityLogService.log(

                currentUser.getId(),

                currentUser.getUsername(),

                currentUser.getRole().getName(),

                ModuleType.PURCHASE,

                ActionType.DELETE,

                buildDeleteDescription(purchase),

                purchase.getId(),

                purchase.getItem().getName(),

                sessionId,

                "SUCCESS"
        );
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
    // DESCRIPTION HELPERS
    // =====================================================

    private String buildCreateDescription(
            Purchase purchase
    ) {

        return "Created purchase for item "
                + purchase.getItem().getName()
                + " with quantity "
                + purchase.getQuantity()
                + " at price ₹"
                + purchase.getPrice();
    }

    private String buildUpdateDescription(
            Purchase purchase
    ) {

        return "Updated purchase for item "
                + purchase.getItem().getName()
                + " with quantity "
                + purchase.getQuantity()
                + " and price ₹"
                + purchase.getPrice();
    }

    private String buildDeleteDescription(
            Purchase purchase
    ) {

        return "Deleted purchase for item "
                + purchase.getItem().getName()
                + " with quantity "
                + purchase.getQuantity();
    }
}