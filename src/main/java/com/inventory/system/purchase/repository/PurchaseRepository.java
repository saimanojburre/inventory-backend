package com.inventory.system.purchase.repository;

import java.math.BigDecimal;
import java.util.List;

import com.inventory.system.purchase.dto.PurchaseResponseDto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.inventory.system.inventory.dto.PurchaseSummary;
import com.inventory.system.purchase.entity.Purchase;

public interface PurchaseRepository
        extends JpaRepository<Purchase, Long> {

    @Query("""
        SELECT new com.inventory.system.inventory.dto.PurchaseSummary(
            p.item.id,
            COALESCE(SUM(p.quantity), 0),
            COALESCE(AVG(p.price), 0)
        )
        FROM Purchase p
        GROUP BY p.item.id
    """)
    List<PurchaseSummary> getPurchaseSummary();

    @Query("""
        SELECT COALESCE(SUM(p.quantity), 0)
        FROM Purchase p
        WHERE p.item.id = :itemId
    """)
    BigDecimal getTotalPurchased(
            @Param("itemId") Long itemId
    );

    @Query("""
    SELECT COALESCE(
        SUM(p.quantity * p.price) / NULLIF(SUM(p.quantity), 0),
        0
    )
    FROM Purchase p
    WHERE p.item.id = :itemId
""")
    BigDecimal getAveragePrice(
            @Param("itemId") Long itemId
    );

    @Query("""
        SELECT new com.inventory.system.purchase.dto.PurchaseResponseDto(
            p.id,
            i.id,
            i.name,
            p.quantity,
            p.price,
            p.supplier,
            p.purchaseDate
        )
        FROM Purchase p
        JOIN p.item i
        ORDER BY p.purchaseDate DESC
    """)
    List<PurchaseResponseDto> getAllPurchaseDtos();
}