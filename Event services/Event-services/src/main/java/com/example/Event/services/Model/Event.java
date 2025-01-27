package com.example.Event.services.Model;

import jakarta.persistence.GeneratedValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "events")
public class Event {
    @Id
    @GeneratedValue(generator = "custom-id-generator")
    @GenericGenerator(
            name = "custom-id-generator",
            strategy = "package com.example.Event.Service.Generator.CustomIdGenerator;"
    )
    private String id;
    private String name;
    private String description;
    private String status; // Pending / Approved
    private String employeeId; // The employee who created the event
    private String venueId; // Venue ID where the event will take place
    private int seatsBooked;
    private int totalSeats;
    private List<String> vendorIds; // List of vendor IDs for food, resources, etc.
    private List<String> resourcePersonIds; // List of resource person IDs (cleaners, security, etc.)
    private Map<String, String> resourcePersonRequests = new HashMap<>(); // Key: resourcePersonId, Value: Status (e.g., Pending, Approved)
    private Map<String, Boolean> seatBookings = new HashMap<>(); // Key: employeeId, Value: true if booked
    private Date eventDate; // The event date
    private List<String> bookedEmployees; // Added for tracking booked employees
}

