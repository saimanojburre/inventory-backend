package com.inventory.system.inventory.dto;

public class UsageSummary {

    private Long itemId;
    private Double totalUsed;

    public UsageSummary(Long itemId, Double totalUsed) {
        this.itemId = itemId;
        this.totalUsed = totalUsed;
    }

    public Long getItemId() {
        return itemId;
    }

    public Double getTotalUsed() {
        return totalUsed;
    }
}