package com.zhanetnikolovska.earthquake.controller;
import java.time.Instant;
import com.zhanetnikolovska.earthquake.model.Earthquake;
import com.zhanetnikolovska.earthquake.service.EarthquakeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/earthquakes")
@CrossOrigin(origins = "http://localhost:5173")
public class EarthquakeController {

    private final EarthquakeService service;

    public EarthquakeController(EarthquakeService service) {
        this.service = service;
    }

    @GetMapping("/fetch")
    public ResponseEntity<List<Earthquake>> fetchAndStore() {
        List<Earthquake> earthquakes = service.fetchAndStore();
        return ResponseEntity.ok(earthquakes);
    }

    @GetMapping
    public ResponseEntity<List<Earthquake>> getAll() {
        List<Earthquake> earthquakes = service.getAll();
        return ResponseEntity.ok(earthquakes);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Earthquake>> getAfterTime(@RequestParam String since) {
        try {
            Instant sinceInstant = Instant.parse(since);
            List<Earthquake> result = service.getAfterTime(sinceInstant);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}