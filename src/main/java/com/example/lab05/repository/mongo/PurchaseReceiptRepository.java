package com.example.lab05.repository.mongo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.lab05.model.mongo.PurchaseReceipt;

public interface PurchaseReceiptRepository extends MongoRepository<PurchaseReceipt, String> {

    List<PurchaseReceipt> findByPersonName(String personName);

    List<PurchaseReceipt> findByProductCategory(String category);
}
