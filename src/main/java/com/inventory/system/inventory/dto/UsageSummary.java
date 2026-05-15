package com.inventory.system.inventory.dto;

import java.math.BigDecimal;

public class UsageSummary {

    private Long itemId;
    private BigDecimal totalUsed;

    public UsageSummary(
            Long itemId,
            BigDecimal totalUsed
    ) {
        this.itemId = itemId;
        this.totalUsed = totalUsed;
    }

    public Long getItemId() {
        return itemId;
    }

    public BigDecimal getTotalUsed() {
        return totalUsed;
    }
}