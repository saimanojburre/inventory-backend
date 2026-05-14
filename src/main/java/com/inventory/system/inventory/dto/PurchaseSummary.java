package com.inventory.system.inventory.dto;

public class PurchaseSummary {

    private Long itemId;
    private Double totalPurchased;
    private Double avgPrice;

    public PurchaseSummary(Long itemId, Double totalPurchased, Double avgPrice) {
        this.itemId = itemId;
        this.totalPurchased = totalPurchased;
        this.avgPrice = avgPrice;
    }

    public Long getItemId() {
        return itemId;
    }

    public Double getTotalPurchased() {
        return totalPurchased;
    }

    public Double getAvgPrice() {
        return avgPrice;
    }
}