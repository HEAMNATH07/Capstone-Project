package com.example.Admin.Service.Config;


import com.example.Admin.Service.DTO.EventDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "event-service") // Name of the Event service
public interface EventClient {

    // Fetch event details by event ID
    @GetMapping("/events/{eventId}")
    EventDTO getEventById(@PathVariable("eventId") String eventId);

    // Update event status to 'approved'
    @PutMapping("/events/{eventId}")
    EventDTO updateEventStatus(@PathVariable("eventId") String eventId, @RequestBody EventDTO event);
}
