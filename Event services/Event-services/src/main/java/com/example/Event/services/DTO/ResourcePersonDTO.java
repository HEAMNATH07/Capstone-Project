package com.example.Event.services.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResourcePersonDTO {
    private String id;
    private String name;
    private String role;
    private String contactInfo;
    private boolean isAvailable;
}
