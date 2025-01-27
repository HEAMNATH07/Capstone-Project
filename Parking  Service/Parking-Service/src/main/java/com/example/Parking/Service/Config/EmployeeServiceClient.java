package com.example.Parking.Service.Config;


import com.example.Parking.Service.DTO.EmployeeDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "employee-service")
public interface EmployeeServiceClient {

    @GetMapping("/employees/{id}")
    EmployeeDTO getEmployeeById(@PathVariable String id);
}
