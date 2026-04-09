package com.example.lab05.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.lab05.dto.PurchaseRequest;
import com.example.lab05.model.mongo.PurchaseReceipt;
import com.example.lab05.service.PurchaseService;

@RestController
@RequestMapping("/${ID}/purchases")
public class PurchaseController {

    private final PurchaseService purchaseService;

    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @PostMapping
    public PurchaseReceipt purchase(@RequestBody PurchaseRequest request) {
        return purchaseService.executePurchase(request);
    }

    @GetMapping("/person/{personName}")
    public List<PurchaseReceipt> receiptsByPerson(@PathVariable String personName) {
        return purchaseService.getReceiptsByPerson(personName);
    }
}
