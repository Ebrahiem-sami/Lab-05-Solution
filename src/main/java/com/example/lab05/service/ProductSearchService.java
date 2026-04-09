package com.example.lab05.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.lab05.model.elastic.ProductDocument;
import com.example.lab05.repository.elastic.ElasticSearchQueryRepository;
import com.example.lab05.repository.elastic.ProductSearchRepository;

@Service
public class ProductSearchService {

    
    private final ProductSearchRepository productSearchRepository;
    private final ElasticSearchQueryRepository elasticSearchQueryRepository;
    public ProductSearchService(ProductSearchRepository productSearchRepository,
                                ElasticSearchQueryRepository elasticSearchQueryRepository) {
        this.productSearchRepository = productSearchRepository;
        this.elasticSearchQueryRepository = elasticSearchQueryRepository;
    }
    public ProductDocument saveProduct(ProductDocument product) {
        return productSearchRepository.save(product);
    }

    public List<ProductDocument> getByCategory(String category) {
        return productSearchRepository.findByCategory(category);
    }

    public List<ProductDocument> searchByName(String name) {
        return productSearchRepository.searchByName(name);
    }
    public List<ProductDocument> search(String query, String category,
                                        Double minPrice, Double maxPrice,
                                        int page, int size) {
        return elasticSearchQueryRepository.search(query, category, minPrice, maxPrice, page, size);
    }
}
