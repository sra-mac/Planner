package com.rocketseat.camila.planner.link;

import com.rocketseat.camila.planner.activity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LinkRepository extends JpaRepository<Link, UUID> {
    List<Activity> findByTripId(UUID tripId);
}
