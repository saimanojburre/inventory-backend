package com.inventory.system.usage.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.inventory.system.usage.entity.Usage;
import com.inventory.system.item.entity.Item;

public interface UsageRepository extends JpaRepository<Usage, Long> {

	List<Usage> findByItem(Item item);

	@Query("SELECT SUM(u.quantity) FROM Usage u WHERE u.item.id = :itemId")
	Double getTotalUsed(Long itemId);

	List<Usage> findByUsedDateTimeBetween(LocalDateTime from, LocalDateTime to);
}