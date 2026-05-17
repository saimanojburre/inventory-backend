package com.inventory.system.purchase.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inventory.system.purchase.dto.PurchaseResponseDto;
import com.inventory.system.purchase.entity.Purchase;
import com.inventory.system.purchase.service.PurchaseService;

@RestController
@RequestMapping("/purchases")
public class PurchaseController {

    private final PurchaseService purchaseService;

    public PurchaseController(
            PurchaseService purchaseService
    ) {

        this.purchaseService = purchaseService;
    }

    // =====================================================
    // CREATE PURCHASE
    // =====================================================

    @PostMapping
    public Purchase createPurchase(

            @RequestBody Purchase purchase,

            @RequestHeader(
                    value = "X-Session-Id",
                    required = false
            )
            String sessionId
    ) {

        return purchaseService.createPurchase(
                purchase,
                sessionId
        );
    }

    // =====================================================
    // BULK PURCHASE
    // =====================================================

    @PostMapping("/bulk")
    public List<Purchase> createPurchases(

            @RequestBody List<Purchase> purchases,

            @RequestHeader(
                    value = "X-Session-Id",
                    required = false
            )
            String sessionId
    ) {

        return purchaseService.createPurchases(
                purchases,
                sessionId
        );
    }

    // =====================================================
    // GET ALL PURCHASES
    // =====================================================

    @GetMapping
    public List<PurchaseResponseDto> getPurchases() {

        return purchaseService.getAllPurchases();
    }

    // =====================================================
    // GET PURCHASE BY ID
    // =====================================================

    @GetMapping("/{id}")
    public Purchase getPurchase(
            @PathVariable Long id
    ) {

        return purchaseService.getPurchase(id);
    }

    // =====================================================
    // UPDATE PURCHASE
    // =====================================================

    @PutMapping("/{id}")
    public Purchase updatePurchase(

            @PathVariable Long id,

            @RequestBody Purchase purchase,

            @RequestHeader(
                    value = "X-Session-Id",
                    required = false
            )
            String sessionId
    ) {

        return purchaseService.updatePurchase(
                id,
                purchase,
                sessionId
        );
    }

    // =====================================================
    // DELETE PURCHASE
    // =====================================================

    @DeleteMapping("/{id}")
    public String deletePurchase(

            @PathVariable Long id,

            @RequestHeader(
                    value = "X-Session-Id",
                    required = false
            )
            String sessionId
    ) {

        purchaseService.deletePurchase(
                id,
                sessionId
        );

        return "Purchase deleted successfully";
    }
}