package com.example.Event.services.Config;

import com.example.Event.services.DTO.VendorDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "vendor-service")
public interface VendorFeignClient {

    @GetMapping("/vendors/{id}")
    VendorDTO getById(@PathVariable String id);
}
