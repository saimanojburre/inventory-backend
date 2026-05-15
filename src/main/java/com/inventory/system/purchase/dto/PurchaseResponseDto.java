package com.inventory.system.purchase.dto;

import java.time.LocalDateTime;

public class PurchaseResponseDto {

    private Long id;
    private Long itemId;
    private String itemName;
    private Double quantity;
    private Double price;
    private String supplier;
    private LocalDateTime purchaseDate;

    public PurchaseResponseDto(
            Long id,
            Long itemId,
            String itemName,
            Double quantity,
            Double price,
            String supplier,
            LocalDateTime purchaseDate
    ) {
        this.id = id;
        this.itemId = itemId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.price = price;
        this.supplier = supplier;
        this.purchaseDate = purchaseDate;
    }

    public Long getId() {
        return id;
    }

    public Long getItemId() {
        return itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public Double getQuantity() {
        return quantity;
    }

    public Double getPrice() {
        return price;
    }

    public String getSupplier() {
        return supplier;
    }

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }
}