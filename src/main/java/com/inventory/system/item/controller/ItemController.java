package com.inventory.system.item.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inventory.system.item.dto.BulkItemResponse;
import com.inventory.system.item.entity.Item;
import com.inventory.system.item.service.ItemService;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(
            ItemService itemService
    ) {

        this.itemService = itemService;
    }

    // =====================================================
    // CREATE ITEM
    // =====================================================

    @PostMapping
    public Item createItem(

            @RequestBody Item item,

            @RequestHeader(
                    value = "X-Session-Id",
                    required = false
            )
            String sessionId
    ) {

        return itemService.createItem(
                item,
                sessionId
        );
    }

    // =====================================================
    // BULK CREATE ITEMS
    // =====================================================

    @PostMapping("/bulk")
    public BulkItemResponse createItems(

            @RequestBody List<Item> items,

            @RequestHeader(
                    value = "X-Session-Id",
                    required = false
            )
            String sessionId
    ) {

        return itemService.createItems(
                items,
                sessionId
        );
    }

    // =====================================================
    // GET ALL ITEMS
    // =====================================================

    @GetMapping
    public List<Item> getItems() {

        return itemService.getAllItems();
    }

    // =====================================================
    // GET ITEM BY ID
    // =====================================================

    @GetMapping("/{id}")
    public Item getItem(
            @PathVariable Long id
    ) {

        return itemService.getItem(id);
    }

    // =====================================================
    // UPDATE ITEM
    // =====================================================

    @PutMapping("/{id}")
    public Item updateItem(

            @PathVariable Long id,

            @RequestBody Item item,

            @RequestHeader(
                    value = "X-Session-Id",
                    required = false
            )
            String sessionId
    ) {

        return itemService.updateItem(
                id,
                item,
                sessionId
        );
    }

    // =====================================================
    // DELETE ITEM
    // =====================================================

    @DeleteMapping("/{id}")
    public Map<String, String> deleteItem(

            @PathVariable Long id,

            @RequestHeader(
                    value = "X-Session-Id",
                    required = false
            )
            String sessionId
    ) {

        itemService.deleteItem(
                id,
                sessionId
        );

        return Map.of(
                "message",
                "Item deleted successfully"
        );
    }
}