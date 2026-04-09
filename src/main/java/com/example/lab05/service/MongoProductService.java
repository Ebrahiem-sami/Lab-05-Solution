package com.example.lab05.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.lab05.dto.CategoryAvgDTO;
import com.example.lab05.model.mongo.MongoProduct;
import com.example.lab05.repository.mongo.MongoAggregationRepository;
import com.example.lab05.repository.mongo.MongoProductRepository;

@Service
public class MongoProductService {

    private final MongoProductRepository mongoProductRepository;
    private final MongoAggregationRepository mongoAggregationRepository;

    public MongoProductService(MongoProductRepository mongoProductRepository, MongoAggregationRepository mongoAggregationRepository) {
        this.mongoProductRepository = mongoProductRepository;
        this.mongoAggregationRepository = mongoAggregationRepository;
    }

    public List<MongoProduct> getAllProducts() {
        return mongoProductRepository.findAll();
    }

    public MongoProduct getProductById(String id) {
        return mongoProductRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found: " + id));
    }

    public MongoProduct createProduct(MongoProduct product) {
        return mongoProductRepository.save(product);
    }

    public void deleteProduct(String id) {
        mongoProductRepository.deleteById(id);
    }

    public List<MongoProduct> getByCategory(String category) {
        return mongoProductRepository.findByCategory(category);
    }

    public List<MongoProduct> getByPriceRange(Double min, Double max) {
        return mongoProductRepository.findByPriceBetween(min, max);
    }

    public List<MongoProduct> getByTag(String tag) {
        return mongoProductRepository.findByTag(tag);
    }

    public List<CategoryAvgDTO> getAverageByCategory() {
        return mongoAggregationRepository.getAverageByCategory();
    }
}
