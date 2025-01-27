package com.example.Venue.Microservices.Repository;

import com.example.Venue.Microservices.Model.Venue;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface VenueRepository extends MongoRepository<Venue, String> {
    Optional<Venue> findByIdAndIsAvailable(String id, boolean isAvailable);
}