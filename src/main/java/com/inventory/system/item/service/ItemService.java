package com.inventory.system.item.service;

import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.inventory.system.exception.BadRequestException;
import com.inventory.system.exception.ResourceNotFoundException;
import com.inventory.system.item.dto.BulkItemResponse;
import com.inventory.system.item.entity.Item;
import com.inventory.system.item.repository.ItemRepository;
import com.inventory.system.user.entity.User;
import com.inventory.system.user.repository.UserRepository;

@Service
public class ItemService {

	private final ItemRepository itemRepository;

	private final UserRepository userRepository;

	public ItemService(ItemRepository itemRepository, UserRepository userRepository) {

		this.itemRepository = itemRepository;
		this.userRepository = userRepository;

	}

	// CREATE ITEM
	public Item createItem(Item item) {

		if (itemRepository.findByName(item.getName()).isPresent()) {
			throw new BadRequestException("Item already exists");
		}

		return itemRepository.save(item);
	}

	public BulkItemResponse createItems(List<Item> items) {

		List<Item> validItems = new ArrayList<>();
		List<String> duplicates = new ArrayList<>();

		Set<String> requestNames = new HashSet<>();

		for (Item item : items) {

			String name = item.getName().toLowerCase();

			// duplicate in request
			if (!requestNames.add(name)) {
				duplicates.add(item.getName());
				continue;
			}

			// duplicate in DB
			if (itemRepository.findByName(item.getName()).isPresent()) {
				duplicates.add(item.getName());
				continue;
			}

			validItems.add(item);
		}

		itemRepository.saveAll(validItems);

		BulkItemResponse response = new BulkItemResponse();
		response.setSaved(validItems.size());
		response.setDuplicates(duplicates);

		return response;
	}

	// GET ALL ITEMS
	public List<Item> getAllItems() {

	    if (SecurityContextHolder.getContext().getAuthentication() == null) {
	        return itemRepository.findAll();
	    }

	    String username = getLoggedInUsername();

	    User user = userRepository.findByUsername(username)
	            .orElseThrow(() -> new RuntimeException("User not found"));

	    if ("USER".equals(user.getRole().getName())) {
	        return itemRepository.findByActiveTrue();
	    }

	    return itemRepository.findAll();
	}

	// GET ITEM BY ID
	public Item getItem(Long id) {

		return itemRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Item not found"));
	}

	// UPDATE ITEM
	public Item updateItem(Long id, Item item) {

		Item existingItem = itemRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Item not found"));

		existingItem.setName(item.getName());
		existingItem.setCategory(item.getCategory());
		existingItem.setUnit(item.getUnit());
		existingItem.setMinStock(item.getMinStock());

		return itemRepository.save(existingItem);
	}

	private String getLoggedInUsername() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}

	// DELETE ITEM (SOFT DELETE)
	public void deleteItem(Long id) {

		Item item = itemRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Item not found"));

		item.setActive(false);

		itemRepository.save(item);
	}
}