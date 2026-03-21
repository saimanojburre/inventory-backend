package com.inventory.system.purchase.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.inventory.system.purchase.entity.Purchase;
import com.inventory.system.item.entity.Item;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

	List<Purchase> findByItem(Item item);

	@Query("SELECT SUM(p.quantity) FROM Purchase p WHERE p.item = :item")
	Double getTotalPurchasedQuantity(Item item);

	@Query("SELECT SUM(p.quantity) FROM Purchase p WHERE p.item.id = :itemId")
	Double getTotalPurchased(Long itemId);

	@Query("SELECT AVG(p.price) FROM Purchase p WHERE p.item.id = :itemId")
	Double getAveragePrice(Long itemId);
}