package com.example.lab05.repository.cassandra;

import java.time.Instant;
import java.util.List;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

import com.example.lab05.model.cassandra.SensorReading;
import com.example.lab05.model.cassandra.SensorReadingKey;

public interface SensorReadingRepository extends CassandraRepository<SensorReading, SensorReadingKey> {
    List<SensorReading> findByKeySensorId(String sensorId);

    @Query("SELECT * FROM sensor_readings " +
           "WHERE sensor_id = ?0 " +
           "AND reading_time >= ?1 AND reading_time <= ?2")
    List<SensorReading> findReadingsInRange(
        String sensorId, Instant from, Instant to);
}
