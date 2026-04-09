package com.example.lab05.repository.neo4j;

import java.util.List;
import java.util.Map;

import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Repository;



@Repository
public class Neo4jGraphRepository {

    
    
    private final Neo4jClient neo4jClient;
    public Neo4jGraphRepository(Neo4jClient neo4jClient) {
        this.neo4jClient = neo4jClient;
    }

    public List<Map<String, Object>> getRecommendations(String personName, int limit) {
        return neo4jClient
            .query("MATCH (p:Person {name: $name})" +
                   "-[:FOLLOWS]->(friend)" +
                   "-[:PURCHASED]->(prod) " +
                   "WHERE NOT (p)-[:PURCHASED]->(prod) " +
                   "RETURN prod.name AS product, " +
                   "       COUNT(friend) AS score " +
                   "ORDER BY score DESC LIMIT $limit")
            .bind(personName).to("name")
            .bind(limit).to("limit")
            .fetch()
            .all()
            .stream().toList();
    }
}
