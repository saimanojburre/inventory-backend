package com.inventory.system.inventory.dto;

import java.math.BigDecimal;

public class PurchaseSummary {

    private Long itemId;

    private BigDecimal totalPurchased;

    private Double avgPrice;

    public PurchaseSummary(
            Long itemId,
            BigDecimal totalPurchased,
            Double avgPrice
    ) {
        this.itemId = itemId;
        this.totalPurchased = totalPurchased;
        this.avgPrice = avgPrice;
    }

    public Long getItemId() {
        return itemId;
    }

    public BigDecimal getTotalPurchased() {
        return totalPurchased;
    }

    public Double getAvgPrice() {
        return avgPrice;
    }
}