package com.inventory.system.purchase.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PurchaseResponseDto {

    private Long id;
    private Long itemId;
    private String itemName;
    private BigDecimal quantity;
    private BigDecimal price;
    private String supplier;
    private LocalDateTime purchaseDate;

    public PurchaseResponseDto(
            Long id,
            Long itemId,
            String itemName,
            BigDecimal quantity,
            BigDecimal price,
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

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getSupplier() {
        return supplier;
    }

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }
}