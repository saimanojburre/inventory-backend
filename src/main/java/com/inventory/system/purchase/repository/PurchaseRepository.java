package com.inventory.system.purchase.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.inventory.system.inventory.dto.PurchaseSummary;
import com.inventory.system.purchase.entity.Purchase;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    @Query("""
        SELECT new com.inventory.system.inventory.dto.PurchaseSummary(
            p.item.id,
            SUM(p.quantity),
            AVG(p.price)
        )
        FROM Purchase p
        GROUP BY p.item.id
    """)
    List<PurchaseSummary> getPurchaseSummary();
    @Query("""
    SELECT SUM(p.quantity)
    FROM Purchase p
    WHERE p.item.id = :itemId
""")
    Double getTotalPurchased(Long itemId);

    @Query("""
    SELECT AVG(p.price)
    FROM Purchase p
    WHERE p.item.id = :itemId
""")
    Double getAveragePrice(Long itemId);
}