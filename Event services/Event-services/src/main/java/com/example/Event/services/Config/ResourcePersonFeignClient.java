package com.example.Event.services.Config;


import com.example.Event.services.DTO.ResourcePersonDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "resource-service")
public interface ResourcePersonFeignClient {

    @GetMapping("/resource-persons/{id}")
    ResourcePersonDTO getResourcePersonById(@PathVariable String id);
}
