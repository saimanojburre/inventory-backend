package com.inventory.system.usage.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.inventory.system.inventory.dto.UsageSummary;
import com.inventory.system.usage.entity.Usage;

public interface UsageRepository extends JpaRepository<Usage, Long> {

    @Query("""
        SELECT new com.inventory.system.inventory.dto.UsageSummary(
            u.item.id,
            SUM(u.quantity)
        )
        FROM Usage u
        GROUP BY u.item.id
    """)
    List<UsageSummary> getUsageSummary();
    @Query("""
    SELECT SUM(u.quantity)
    FROM Usage u
    WHERE u.item.id = :itemId
""")
    Double getTotalUsed(Long itemId);

    List<Usage> findByUsedDateTimeBetween(
            LocalDateTime from,
            LocalDateTime to
    );
}