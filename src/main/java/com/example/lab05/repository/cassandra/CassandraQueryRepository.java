package com.example.lab05.repository.cassandra;

import java.util.List;

import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.core.query.Criteria;
import org.springframework.data.cassandra.core.query.Query;
import org.springframework.stereotype.Repository;

import com.example.lab05.model.cassandra.SensorReading;



@Repository
public class CassandraQueryRepository {

    private final CassandraTemplate cassandraTemplate;

    public CassandraQueryRepository(CassandraTemplate cassandraTemplate) {
        this.cassandraTemplate = cassandraTemplate;
    }

    public List<SensorReading> findLatestReadings(String sensorId, int limit) {
        Query query = Query.query(Criteria.where("sensor_id").is(sensorId))
                .limit(limit);
        List<SensorReading> results = cassandraTemplate.select(query, SensorReading.class);
        return results;
    }
}
