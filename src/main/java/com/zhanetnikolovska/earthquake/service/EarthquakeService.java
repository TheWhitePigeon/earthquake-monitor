package com.zhanetnikolovska.earthquake.service;

import com.zhanetnikolovska.earthquake.model.Earthquake;
import com.zhanetnikolovska.earthquake.repository.EarthquakeRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class EarthquakeService {

    private static final String USGS_URL =
            "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.geojson";

    private final EarthquakeRepository repository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public EarthquakeService(EarthquakeRepository repository) {
        this.repository = repository;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public List<Earthquake> fetchAndStore() {
        String response;
        try {
            response = restTemplate.getForObject(USGS_URL, String.class);
        } catch (RestClientException e) {
            throw new RuntimeException("Failed to reach USGS API: " + e.getMessage());
        }

        List<Earthquake> earthquakes = new ArrayList<>();

        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode features = root.path("features");

            for (JsonNode feature : features) {
                JsonNode props = feature.path("properties");
                JsonNode geometry = feature.path("geometry");

                JsonNode magNode = props.path("mag");
                if (magNode.isNull() || magNode.isMissingNode()) continue;

                double magnitude = magNode.asDouble();
                if (magnitude <= 2.0) continue;

                Earthquake eq = new Earthquake();
                eq.setMagnitude(magnitude);
                eq.setMagType(props.path("magType").asText(null));
                eq.setPlace(props.path("place").asText(null));
                eq.setTitle(props.path("title").asText(null));

                JsonNode timeNode = props.path("time");
                if (!timeNode.isNull() && !timeNode.isMissingNode()) {
                    eq.setTime(Instant.ofEpochMilli(timeNode.asLong()));
                }

                JsonNode coords = geometry.path("coordinates");
                if (coords.isArray() && coords.size() >= 2) {
                    eq.setLongitude(coords.get(0).asDouble());
                    eq.setLatitude(coords.get(1).asDouble());
                }

                earthquakes.add(eq);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse USGS response: " + e.getMessage());
        }

        repository.deleteAll();
        repository.saveAll(earthquakes);

        return earthquakes;
    }

    public List<Earthquake> getAll() {
        return repository.findAll();
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public List<Earthquake> getAfterTime(Instant since) {
        return repository.findAll()
                .stream()
                .filter(eq -> eq.getTime() != null && eq.getTime().isAfter(since))
                .collect(java.util.stream.Collectors.toList());
    }
}
