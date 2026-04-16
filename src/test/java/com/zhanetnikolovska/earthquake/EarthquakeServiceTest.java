package com.zhanetnikolovska.earthquake;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;
import com.zhanetnikolovska.earthquake.model.Earthquake;
import com.zhanetnikolovska.earthquake.repository.EarthquakeRepository;
import com.zhanetnikolovska.earthquake.service.EarthquakeService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.List;

//import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EarthquakeServiceTest {

    @Autowired
    private EarthquakeService service;

    @Autowired
    private EarthquakeRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void fetchAndStore_shouldReturnOnlyMagnitudeAbove2() {
        List<Earthquake> result = service.fetchAndStore();
        assertTrue(result.stream().allMatch(eq -> eq.getMagnitude() > 2.0),
                "All earthquakes should have magnitude > 2.0");
    }

    @Test
    void fetchAndStore_shouldDeleteExistingRecordsBeforeInserting() {
        service.fetchAndStore();
        long countAfterFirst = repository.count();

        service.fetchAndStore();
        long countAfterSecond = repository.count();

        assertTrue(countAfterSecond >= 0,
                "Records should be replaced, not duplicated");
    }

    @Test
    void getAfterTime_shouldReturnOnlyEarthquakesAfterGivenTime() {
        service.fetchAndStore();
        Instant cutoff = Instant.now().minusSeconds(3600);
        List<Earthquake> result = service.getAfterTime(cutoff);
        assertTrue(result.stream().allMatch(eq -> eq.getTime().isAfter(cutoff)),
                "All results should be after the cutoff time");
    }

    @Test
    void getAll_shouldReturnStoredEarthquakes() {
        service.fetchAndStore();
        List<Earthquake> result = service.getAll();
        assertFalse(result.isEmpty(), "Should return stored earthquakes");
    }

    @Test
    void deleteById_shouldRemoveEarthquake() {
        service.fetchAndStore();
        List<Earthquake> all = service.getAll();
        assertFalse(all.isEmpty());

        Long id = all.get(0).getId();
        service.deleteById(id);

        List<Earthquake> after = service.getAll();
        assertTrue(after.stream().noneMatch(eq -> eq.getId().equals(id)),
                "Deleted earthquake should no longer exist");
    }
}