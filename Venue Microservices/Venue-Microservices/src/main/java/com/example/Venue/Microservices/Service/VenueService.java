package com.example.Venue.Microservices.Service;

import com.example.Venue.Microservices.Model.Venue;
import com.example.Venue.Microservices.Repository.VenueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VenueService {

    @Autowired
    private VenueRepository venueRepository;

    public Venue addVenue(Venue venue) {
        return venueRepository.save(venue);
    }

    public List<Venue> getAllVenues() {
        return venueRepository.findAll();
    }

    public Venue getVenueById(String id) {
        return venueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venue not found with id: " + id));
    }

    public boolean isVenueAvailable(String id) {
        return venueRepository.findByIdAndIsAvailable(id, true).isPresent();
    }

    public Venue updateVenue(String id, Venue venueDetails) {
        Venue existingVenue = getVenueById(id);
        existingVenue.setName(venueDetails.getName());
        existingVenue.setLocation(venueDetails.getLocation());
        existingVenue.setCapacity(venueDetails.getCapacity());
        existingVenue.setAvailable(venueDetails.isAvailable());
        return venueRepository.save(existingVenue);
    }

    public void deleteVenue(String id) {
        venueRepository.deleteById(id);
    }
}