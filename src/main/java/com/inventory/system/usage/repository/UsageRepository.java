package com.inventory.system.usage.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.inventory.system.inventory.dto.UsageSummary;
import com.inventory.system.usage.entity.Usage;

public interface UsageRepository
        extends JpaRepository<Usage, Long> {

    @Query("""
        SELECT new com.inventory.system.inventory.dto.UsageSummary(
            u.item.id,
            COALESCE(SUM(u.quantity), 0)
        )
        FROM Usage u
        GROUP BY u.item.id
    """)
    List<UsageSummary> getUsageSummary();

    @Query("""
        SELECT COALESCE(SUM(u.quantity), 0)
        FROM Usage u
        WHERE u.item.id = :itemId
    """)
    BigDecimal getTotalUsed(
            @Param("itemId") Long itemId
    );

    List<Usage> findByUsedDateTimeBetween(
            LocalDateTime from,
            LocalDateTime to
    );
}