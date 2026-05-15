package com.inventory.system.item.service;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public ItemService(
            ItemRepository itemRepository,
            UserRepository userRepository
    ) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    // CREATE ITEM
    public Item createItem(Item item) {

        if (itemRepository.existsByName(item.getName())) {

            throw new BadRequestException(
                    "Item already exists"
            );
        }

        return itemRepository.save(item);
    }

    // BULK CREATE ITEMS
    public BulkItemResponse createItems(List<Item> items) {

        List<String> duplicates = new ArrayList<>();
        List<Item> validItems = new ArrayList<>();

        // Request names
        Set<String> requestNames = new HashSet<>();

        // Existing DB names
        Set<String> existingNames = itemRepository.findAll()
                .stream()
                .map(item -> item.getName().toLowerCase())
                .collect(Collectors.toSet());

        for (Item item : items) {

            String normalizedName =
                    item.getName().trim().toLowerCase();

            // Duplicate inside request
            if (!requestNames.add(normalizedName)) {

                duplicates.add(item.getName());

                continue;
            }

            // Duplicate in DB
            if (existingNames.contains(normalizedName)) {

                duplicates.add(item.getName());

                continue;
            }

            validItems.add(item);
        }

        itemRepository.saveAll(validItems);

        BulkItemResponse response =
                new BulkItemResponse();

        response.setSaved(validItems.size());

        response.setDuplicates(duplicates);

        return response;
    }

    // GET ALL ITEMS
    @Transactional(readOnly = true)
    public List<Item> getAllItems() {

        String username = getLoggedInUsername();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );

        if ("USER".equals(user.getRole().getName())) {

            return itemRepository.findByActiveTrue();
        }

        return itemRepository.findAll();
    }

    // GET ITEM BY ID
    @Transactional(readOnly = true)
    public Item getItem(Long id) {

        return itemRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Item not found"
                        )
                );
    }

    // UPDATE ITEM
    public Item updateItem(Long id, Item item) {

        Item existingItem = itemRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Item not found"
                        )
                );

        existingItem.setName(item.getName());
        existingItem.setCategory(item.getCategory());
        existingItem.setUnit(item.getUnit());
        existingItem.setMinStock(item.getMinStock());

        return itemRepository.save(existingItem);
    }

    // SOFT DELETE
    public void deleteItem(Long id) {

        Item item = itemRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Item not found"
                        )
                );

        item.setActive(false);

        itemRepository.save(item);
    }

    private String getLoggedInUsername() {

        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
    }
}