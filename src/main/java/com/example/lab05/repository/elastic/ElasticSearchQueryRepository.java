package com.example.lab05.repository.elastic;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Repository;

import com.example.lab05.model.elastic.ProductDocument;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.NumberRangeQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;



@Repository
public class ElasticSearchQueryRepository {

    private final ElasticsearchOperations elasticsearchOperations;
    public ElasticSearchQueryRepository(ElasticsearchOperations elasticsearchOperations) {
        this.elasticsearchOperations = elasticsearchOperations;
    }

    public List<ProductDocument> search(
            String query, String category,
            Double minPrice, Double maxPrice,
            int page, int size) {
        NativeQuery nativeQuery = new NativeQueryBuilder()
            .withQuery(q -> q
                .bool(b -> {
                    b.must(m -> m
                        .multiMatch(mm -> mm
                            .fields("name^3", "description")
                            .query(query)
                            .fuzziness("AUTO")
                        )
                    );
                    if (category != null && !category.isEmpty()) {
                        b.filter(f -> f
                            .term(t -> t
                                .field("category")
                                .value(category)
                            )
                        );
                    }
                    if (minPrice != null || maxPrice != null) {
                        b.filter(f -> f
                            .range(r -> r
                                .number(n -> {
                                    n.field("price");
                                    if (minPrice != null) n.gte(minPrice);
                                    if (maxPrice != null) n.lte(maxPrice);
                                    return n;
                                })
                            )
                        );
                    }
                    return b;
                })
            )
            .withPageable(PageRequest.of(page, size))
            .build();
        
        return elasticsearchOperations.search(nativeQuery, ProductDocument.class)
                .map(hit -> hit.getContent())
                .toList();
    }

    public List<ProductDocument> searchSimplified(
            String query, String category,
            Double minPrice, Double maxPrice,
            int page, int size) {

        MultiMatchQuery multiMatch = new MultiMatchQuery.Builder()
                .fields("name^3", "description")
                .query(query)
                .fuzziness("AUTO")
                .build();

        Query mustQuery = new Query.Builder()
                .multiMatch(multiMatch)
                .build();

        BoolQuery.Builder boolBuilder = new BoolQuery.Builder();
        boolBuilder.must(mustQuery);

        if (category != null && !category.isEmpty()) {
            TermQuery termQuery = new TermQuery.Builder()
                    .field("category")
                    .value(category)
                    .build();

            Query filterQuery = new Query.Builder()
                    .term(termQuery)
                    .build();

            boolBuilder.filter(filterQuery);
        }

        if (minPrice != null || maxPrice != null) {
            NumberRangeQuery.Builder numberRange = new NumberRangeQuery.Builder();
            numberRange.field("price");
            if (minPrice != null) numberRange.gte(minPrice);
            if (maxPrice != null) numberRange.lte(maxPrice);

            RangeQuery rangeQuery = new RangeQuery.Builder()
                    .number(numberRange.build())
                    .build();

            Query rangeFilter = new Query.Builder()
                    .range(rangeQuery)
                    .build();

            boolBuilder.filter(rangeFilter);
        }

        Query finalQuery = new Query.Builder()
                .bool(boolBuilder.build())
                .build();

        NativeQuery nativeQuery = new NativeQueryBuilder()
                .withQuery(finalQuery)
                .withPageable(PageRequest.of(page, size))
                .build();

        SearchHits<ProductDocument> searchHits =
                elasticsearchOperations.search(nativeQuery, ProductDocument.class);

        List<ProductDocument> results = new ArrayList<>();
        for (SearchHit<ProductDocument> hit : searchHits) {
            results.add(hit.getContent());
        }
        return results;
    }
}
