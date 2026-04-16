package com.zhanetnikolovska.earthquake.repository;

import com.zhanetnikolovska.earthquake.model.Earthquake;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EarthquakeRepository extends JpaRepository<Earthquake, Long> {
}
