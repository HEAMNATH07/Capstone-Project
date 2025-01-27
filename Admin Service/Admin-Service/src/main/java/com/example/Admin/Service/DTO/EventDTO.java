package com.example.Admin.Service.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventDTO {
    private String id;
    private String name;
    private String description;
    private String status; // Pending / Approved
    private String employeeId;
    private String venueId;
    private int seatsBooked;
    private int totalSeats;
    private List<String> vendorIds;
    private List<String> resourcePersonIds;
}