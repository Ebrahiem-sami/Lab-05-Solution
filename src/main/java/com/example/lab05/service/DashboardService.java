package com.example.lab05.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.lab05.dto.DashboardResponse;
import com.example.lab05.model.cassandra.SensorReading;
import com.example.lab05.model.elastic.ProductDocument;
import com.example.lab05.model.mongo.PurchaseReceipt;
import com.example.lab05.model.neo4j.Person;
import com.example.lab05.repository.mongo.PurchaseReceiptRepository;

@Service
public class DashboardService {

    private static final Logger log = LoggerFactory.getLogger(DashboardService.class);

    private final PurchaseReceiptRepository purchaseReceiptRepository;
    private final SocialGraphService socialGraphService;
    private final SensorService sensorService;
    private final ProductSearchService productSearchService;
    private final RedisTemplate<String, Object> redisTemplate;

    public DashboardService(PurchaseReceiptRepository purchaseReceiptRepository,
            SocialGraphService socialGraphService,
            SensorService sensorService,
            ProductSearchService productSearchService,
            RedisTemplate<String, Object> redisTemplate) {
        this.purchaseReceiptRepository = purchaseReceiptRepository;
        this.socialGraphService = socialGraphService;
        this.sensorService = sensorService;
        this.productSearchService = productSearchService;
        this.redisTemplate = redisTemplate;
    }

    public DashboardResponse getDashboard(String personName) {
        String cacheKey = "dashboard:" + personName;

        try {
            Object raw = redisTemplate.opsForValue().get(cacheKey);
            if (raw instanceof DashboardResponse cached) {
                return new DashboardResponse(
                        cached.personName(),
                        cached.totalSpent(),
                        cached.purchaseCount(),
                        cached.recentPurchases(),
                        cached.friendRecommendations(),
                        cached.friendsOfFriends(),
                        cached.recentActivity(),
                        cached.youMightAlsoLike(),
                        true);
            }
        } catch (Exception e) {
            log.warn("Redis cache check failed for {}: {}",
                    personName, e.getMessage());
        }

        List<PurchaseReceipt> allReceipts = purchaseReceiptRepository.findByPersonName(personName);
        double totalSpent = allReceipts.stream()
                .mapToDouble(r -> r.getTotalPrice() != null ? r.getTotalPrice() : 0.0)
                .sum();
        int purchaseCount = allReceipts.size();
        List<PurchaseReceipt> recentPurchases = allReceipts.stream()
                .sorted(Comparator.comparing(PurchaseReceipt::getPurchasedAt,
                        Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .limit(5)
                .toList();

        List<Map<String, Object>> friendRecommendations = List.of();
        List<String> friendsOfFriends = List.of();
        try {
            friendRecommendations = socialGraphService.getRecommendations(personName, 5);
            friendsOfFriends = socialGraphService.getFriendsOfFriends(personName).stream()
                    .map(Person::getName)
                    .toList();
        } catch (Exception e) {
            log.warn("Failed to fetch Neo4j data for {}: {}",
                    personName, e.getMessage());
        }

        List<SensorReading> recentActivity = List.of();
        try {
            recentActivity = sensorService.getLatestReadings(
                    "user-activity-" + personName.toLowerCase(), 10);
        } catch (Exception e) {
            log.warn("Failed to fetch activity for {}: {}",
                    personName, e.getMessage());
        }

        Set<String> alreadyPurchased = allReceipts.stream()
                .map(PurchaseReceipt::getProductName)
                .collect(Collectors.toCollection(HashSet::new));
        Set<String> categories = allReceipts.stream()
                .map(PurchaseReceipt::getProductCategory)
                .filter(c -> c != null && !c.isEmpty())
                .collect(Collectors.toCollection(HashSet::new));

        List<String> youMightAlsoLike = new ArrayList<>();
        try {
            for (String category : categories) {
                List<ProductDocument> inCategory = productSearchService.getByCategory(category);
                int added = 0;
                for (ProductDocument doc : inCategory) {
                    if (added >= 2) {
                        break;
                    }
                    String name = doc.getName();
                    if (name != null && !alreadyPurchased.contains(name)) {
                        youMightAlsoLike.add(name);
                        added++;
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Failed to fetch ES suggestions for {}: {}",
                    personName, e.getMessage());
        }

        DashboardResponse response = new DashboardResponse(
                personName,
                totalSpent,
                purchaseCount,
                recentPurchases,
                friendRecommendations,
                friendsOfFriends,
                recentActivity,
                youMightAlsoLike,
                false);

        try {
            redisTemplate.opsForValue().set(cacheKey, response, Duration.ofMinutes(5));
        } catch (Exception e) {
            log.warn("Failed to cache dashboard for {}: {}",
                    personName, e.getMessage());
        }

        return response;
    }
}
