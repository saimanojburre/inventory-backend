package com.inventory.system;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class InventorySystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventorySystemApplication.class, args);
	}
    @PostConstruct
    void init() {
        TimeZone.setDefault(
                TimeZone.getTimeZone("Asia/Kolkata")
        );
    }
}
