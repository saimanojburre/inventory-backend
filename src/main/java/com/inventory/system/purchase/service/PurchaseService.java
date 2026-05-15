package com.inventory.system.purchase.service;

import java.util.List;

import com.inventory.system.purchase.dto.PurchaseResponseDto;
import org.springframework.stereotype.Service;

import com.inventory.system.exception.ResourceNotFoundException;
import com.inventory.system.item.entity.Item;
import com.inventory.system.item.repository.ItemRepository;
import com.inventory.system.purchase.entity.Purchase;
import com.inventory.system.purchase.repository.PurchaseRepository;

@Service
public class PurchaseService {

	private final PurchaseRepository purchaseRepository;
	private final ItemRepository itemRepository;

	public PurchaseService(PurchaseRepository purchaseRepository, ItemRepository itemRepository) {

		this.purchaseRepository = purchaseRepository;
		this.itemRepository = itemRepository;
	}

	// CREATE PURCHASE
	public Purchase createPurchase(Purchase purchase) {

		Long itemId = purchase.getItem().getId();

		Item item = itemRepository.findById(itemId).orElseThrow(() -> new ResourceNotFoundException("Item not found"));

		purchase.setItem(item);

		return purchaseRepository.save(purchase);
	}

	// BULK PURCHASE
	public List<Purchase> createPurchases(List<Purchase> purchases) {

		for (Purchase purchase : purchases) {

			Long itemId = purchase.getItem().getId();

			Item item = itemRepository.findById(itemId)
					.orElseThrow(() -> new ResourceNotFoundException("Item not found"));

			purchase.setItem(item);
		}

		return purchaseRepository.saveAll(purchases);
	}

	// GET ALL PURCHASES
    public List<PurchaseResponseDto> getAllPurchases() {

        return purchaseRepository.getAllPurchaseDtos();
    }

	// GET PURCHASE BY ID
	public Purchase getPurchase(Long id) {

		return purchaseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Purchase not found"));
	}

	// UPDATE PURCHASE
	public Purchase updatePurchase(Long id, Purchase purchase) {

		Purchase existing = purchaseRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Purchase not found"));

		existing.setQuantity(purchase.getQuantity());
		existing.setPrice(purchase.getPrice());
		existing.setSupplier(purchase.getSupplier());
		existing.setPurchaseDate(purchase.getPurchaseDate());

		return purchaseRepository.save(existing);
	}

	// DELETE PURCHASE
	public void deletePurchase(Long id) {

		Purchase purchase = purchaseRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Purchase not found"));

		purchaseRepository.delete(purchase);
	}
}