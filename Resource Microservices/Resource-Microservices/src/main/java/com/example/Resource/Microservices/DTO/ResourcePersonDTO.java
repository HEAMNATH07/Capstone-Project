package com.example.Resource.Microservices.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourcePersonDTO {
    private String id;
    private String name;
    private String role;
    private String contactInfo;
    private boolean isAvailable;
}
