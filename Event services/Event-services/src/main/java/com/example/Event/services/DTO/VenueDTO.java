package com.example.Event.services.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VenueDTO {
    private String id;
    private String name;
    private String location;
    private int capacity;
    private boolean isAvailable;
}
