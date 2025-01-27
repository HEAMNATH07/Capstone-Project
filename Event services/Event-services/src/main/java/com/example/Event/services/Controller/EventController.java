package com.example.Event.services.Controller;

import com.example.Event.services.Model.Event;
import com.example.Event.services.Service.EventService;
import com.example.Event.services.DTO.VenueDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventService eventService;

    // Create a new event
    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody Event event) {
        try {
            Event createdEvent = eventService.createEvent(event);
            return new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get event details by ID
    @GetMapping("/{eventId}")
    public ResponseEntity<Event> getEvent(@PathVariable String eventId) {
        try {
            Event event = eventService.getEvent(eventId);
            return new ResponseEntity<>(event, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // Approve event by ID
    @PutMapping("/{eventId}/approve")
    public ResponseEntity<Event> approveEvent(@PathVariable String eventId) {
        try {
            Event event = eventService.approveEvent(eventId);
            return new ResponseEntity<>(event, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // Add a vendor to the event
    @PutMapping("/{eventId}/vendor/{vendorId}")
    public ResponseEntity<Event> addVendor(@PathVariable String eventId, @PathVariable String vendorId) {
        try {
            Event event = eventService.addVendor(eventId, vendorId);
            return new ResponseEntity<>(event, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Request a resource person for the event
    @PutMapping("/{eventId}/resourcePerson/{resourcePersonId}")
    public ResponseEntity<Event> requestResourcePerson(@PathVariable String eventId, @PathVariable String resourcePersonId) {
        try {
            Event event = eventService.requestResourcePerson(eventId, resourcePersonId);
            return new ResponseEntity<>(event, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Approve or reject a resource person request
    @PutMapping("/{eventId}/resourcePerson/{resourcePersonId}/approve")
    public ResponseEntity<Event> approveResourcePerson(@PathVariable String eventId, @PathVariable String resourcePersonId, @RequestParam boolean isApproved) {
        try {
            Event event = eventService.approveResourcePerson(eventId, resourcePersonId, isApproved);
            return new ResponseEntity<>(event, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Book seats for the event
    @PostMapping("/{eventId}/bookSeats")
    public ResponseEntity<Event> bookSeats(@PathVariable String eventId, @RequestParam int seats, @RequestParam String userEmail, @RequestParam String employeeId) {
        try {
            Event event = eventService.bookSeats(eventId, seats, userEmail, employeeId);
            return new ResponseEntity<>(event, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // Check if a venue is available for booking
    @GetMapping("/venue/{venueId}/availability")
    public ResponseEntity<Boolean> checkVenueAvailability(@PathVariable String venueId, @RequestParam int seatsRequired) {
        try {
            boolean isAvailable = eventService.isVenueAvailable(venueId, seatsRequired);
            return new ResponseEntity<>(isAvailable, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
    }

    // Get all events (for admin or managers)
    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents() {
        try {
            List<Event> events = eventService.getAllEvents();
            return new ResponseEntity<>(events, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Delete an event by ID (admin functionality)
    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable String eventId) {
        try {
            eventService.deleteEvent(eventId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Update event details
    @PutMapping("/{eventId}")
    public ResponseEntity<Event> updateEvent(@PathVariable String eventId, @RequestBody Event eventDetails) {
        try {
            Event updatedEvent = eventService.updateEvent(eventId, eventDetails);
            return new ResponseEntity<>(updatedEvent, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
