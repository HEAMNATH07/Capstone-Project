package com.example.Venue.Microservices.Controller;


import com.example.Venue.Microservices.Model.Venue;
import com.example.Venue.Microservices.Service.VenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/venues")
public class VenueController {

    @Autowired
    private VenueService venueService;

    @PostMapping
    public Venue addVenue(@RequestBody Venue venue) {
        return venueService.addVenue(venue);
    }

    @GetMapping
    public List<Venue> getAllVenues() {
        return venueService.getAllVenues();
    }

    @GetMapping("/{id}")
    public Venue getVenueById(@PathVariable String id) {
        return venueService.getVenueById(id);
    }

    @GetMapping("/{id}/availability")
    public boolean isVenueAvailable(@PathVariable String id) {
        return venueService.isVenueAvailable(id);
    }

    @PutMapping("/{id}")
    public Venue updateVenue(@PathVariable String id, @RequestBody Venue venueDetails) {
        return venueService.updateVenue(id, venueDetails);
    }

    @DeleteMapping("/{id}")
    public void deleteVenue(@PathVariable String id) {
        venueService.deleteVenue(id);
    }
}
