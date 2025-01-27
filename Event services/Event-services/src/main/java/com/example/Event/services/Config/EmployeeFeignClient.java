package com.example.Event.services.Config;


import com.example.Event.services.DTO.EmployeeDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "employee-service")
public interface EmployeeFeignClient {

    @GetMapping("/employees/{id}")
    EmployeeDTO getEmployeeById(@PathVariable String id);
}
