package com.inventory.system.item.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inventory.system.item.entity.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {

	Optional<Item> findByName(String name);

	List<Item> findByActiveTrue();

}