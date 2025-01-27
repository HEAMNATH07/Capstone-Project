package com.example.Event.services.Config;


import com.example.Event.services.DTO.VenueDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "venue-service")
public interface VenueFeignClient {

    @GetMapping("/venues/{id}")
    VenueDTO getVenueById (@PathVariable String id);
}
