package com.inventory.system.item.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventory.system.activity.enums.ActionType;
import com.inventory.system.activity.enums.ModuleType;
import com.inventory.system.activity.service.ActivityLogService;
import com.inventory.system.exception.BadRequestException;
import com.inventory.system.exception.ResourceNotFoundException;
import com.inventory.system.item.dto.BulkItemResponse;
import com.inventory.system.item.entity.Item;
import com.inventory.system.item.repository.ItemRepository;
import com.inventory.system.user.entity.User;
import com.inventory.system.user.repository.UserRepository;

@Service
@Transactional
public class ItemService {

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final ActivityLogService activityLogService;

    public ItemService(
            ItemRepository itemRepository,
            UserRepository userRepository,
            ActivityLogService activityLogService
    ) {

        this.itemRepository = itemRepository;

        this.userRepository = userRepository;

        this.activityLogService = activityLogService;
    }

    // =====================================================
    // CREATE ITEM
    // =====================================================

    public Item createItem(
            Item item,
            String sessionId
    ) {

        if (itemRepository.existsByName(item.getName())) {

            throw new BadRequestException(
                    "Item already exists"
            );
        }

        Item savedItem =
                itemRepository.save(item);

        User currentUser = getCurrentUser();

        activityLogService.log(

                currentUser.getId(),

                currentUser.getUsername(),

                currentUser.getRole().getName(),

                ModuleType.ITEM,

                ActionType.CREATE,

                "Created item "
                        + savedItem.getName(),

                savedItem.getId(),

                savedItem.getName(),

                sessionId,

                "SUCCESS"
        );

        return savedItem;
    }

    // =====================================================
    // BULK CREATE ITEMS
    // =====================================================

    public BulkItemResponse createItems(
            List<Item> items,
            String sessionId
    ) {

        List<String> duplicates =
                new ArrayList<>();

        List<Item> validItems =
                new ArrayList<>();

        Set<String> requestNames =
                new HashSet<>();

        Set<String> existingNames =
                itemRepository.findAll()
                        .stream()
                        .map(item ->
                                item.getName()
                                        .toLowerCase()
                        )
                        .collect(Collectors.toSet());

        for (Item item : items) {

            String normalizedName =
                    item.getName()
                            .trim()
                            .toLowerCase();

            // DUPLICATE IN REQUEST

            if (!requestNames.add(normalizedName)) {

                duplicates.add(item.getName());

                continue;
            }

            // DUPLICATE IN DB

            if (existingNames.contains(normalizedName)) {

                duplicates.add(item.getName());

                continue;
            }

            validItems.add(item);
        }

        itemRepository.saveAll(validItems);

        User currentUser = getCurrentUser();

        activityLogService.log(

                currentUser.getId(),

                currentUser.getUsername(),

                currentUser.getRole().getName(),

                ModuleType.ITEM,

                ActionType.CREATE,

                "Created bulk items count: "
                        + validItems.size(),

                null,

                "Bulk Items",

                sessionId,

                "SUCCESS"
        );

        BulkItemResponse response =
                new BulkItemResponse();

        response.setSaved(validItems.size());

        response.setDuplicates(duplicates);

        return response;
    }

    // =====================================================
    // GET ALL ITEMS
    // =====================================================

    @Transactional(readOnly = true)
    public List<Item> getAllItems() {

        User user = getCurrentUser();

        if ("USER".equals(
                user.getRole().getName()
        )) {

            return itemRepository.findByActiveTrue();
        }

        return itemRepository.findAll();
    }

    // =====================================================
    // GET ITEM BY ID
    // =====================================================

    @Transactional(readOnly = true)
    public Item getItem(
            Long id
    ) {

        return itemRepository.findById(id)
                .orElseThrow(() ->

                        new ResourceNotFoundException(
                                "Item not found"
                        )
                );
    }

    // =====================================================
    // UPDATE ITEM
    // =====================================================

    public Item updateItem(

            Long id,

            Item item,

            String sessionId
    ) {

        Item existingItem =
                itemRepository.findById(id)
                        .orElseThrow(() ->

                                new ResourceNotFoundException(
                                        "Item not found"
                                )
                        );

        existingItem.setName(
                item.getName()
        );

        existingItem.setCategory(
                item.getCategory()
        );

        existingItem.setUnit(
                item.getUnit()
        );

        existingItem.setMinStock(
                item.getMinStock()
        );

        Item updatedItem =
                itemRepository.save(existingItem);

        User currentUser =
                getCurrentUser();

        activityLogService.log(

                currentUser.getId(),

                currentUser.getUsername(),

                currentUser.getRole().getName(),

                ModuleType.ITEM,

                ActionType.UPDATE,

                "Updated item "
                        + updatedItem.getName(),

                updatedItem.getId(),

                updatedItem.getName(),

                sessionId,

                "SUCCESS"
        );

        return updatedItem;
    }

    // =====================================================
    // DELETE ITEM
    // =====================================================

    public void deleteItem(

            Long id,

            String sessionId
    ) {

        Item item =
                itemRepository.findById(id)
                        .orElseThrow(() ->

                                new ResourceNotFoundException(
                                        "Item not found"
                                )
                        );

        item.setActive(false);

        itemRepository.save(item);

        User currentUser =
                getCurrentUser();

        activityLogService.log(

                currentUser.getId(),

                currentUser.getUsername(),

                currentUser.getRole().getName(),

                ModuleType.ITEM,

                ActionType.DELETE,

                "Deleted item "
                        + item.getName(),

                item.getId(),

                item.getName(),

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
    // USERNAME
    // =====================================================

    private String getLoggedInUsername() {

        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
    }
}