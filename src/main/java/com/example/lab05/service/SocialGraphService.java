package com.example.lab05.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.lab05.model.neo4j.Neo4jProduct;
import com.example.lab05.model.neo4j.Person;
import com.example.lab05.model.neo4j.Purchased;
import com.example.lab05.repository.neo4j.Neo4jGraphRepository;
import com.example.lab05.repository.neo4j.PersonRepository;

@Service
public class SocialGraphService {

    private final PersonRepository personRepository;
    private final Neo4jGraphRepository neo4jGraphRepository;
    public SocialGraphService(PersonRepository personRepository, Neo4jGraphRepository neo4jGraphRepository) {
        this.personRepository = personRepository;
        this.neo4jGraphRepository = neo4jGraphRepository;
    }

    public Person createPerson(String name) {
        Person person = new Person(name);
        return personRepository.save(person);
    }

    public Person follow(String followerName, String followeeName) {
        Person follower = personRepository.findByName(followerName)
            .orElseThrow(() -> new RuntimeException("Follower not found: " + followerName));
        Person followee = personRepository.findByName(followeeName)
            .orElseThrow(() -> new RuntimeException("Followee not found: " + followeeName));
        follower.getFollowing().add(followee);
        personRepository.save(follower);
        return follower;
    }

    public Person purchase(String personName, String productName, Integer quantity, Double price) {
        Person person = personRepository.findByName(personName)
            .orElseThrow(() -> new RuntimeException("Person not found: " + personName));
        Neo4jProduct product = new Neo4jProduct(productName, price);
        Purchased purchased = new Purchased(product, quantity);
        person.getPurchases().add(purchased);
        personRepository.save(person);
        return person;
    }

    public List<Person> getFriendsOfFriends(String personName) {
        return personRepository.findFriendsOfFriends(personName);
    }

    public List<Map<String, Object>> getRecommendations(String personName, int limit) {
        return neo4jGraphRepository.getRecommendations(personName, limit);
    }
}
