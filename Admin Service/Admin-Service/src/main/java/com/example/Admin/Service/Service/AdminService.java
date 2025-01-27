package com.example.Admin.Service.Service;

import com.example.Admin.Service.Config.EventClient;
import com.example.Admin.Service.DTO.EventDTO;
import com.example.Admin.Service.Model.Admin;
import com.example.Admin.Service.Repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private EventClient eventClient; // Feign Client to communicate with the Event service

    // Register new admin
    public Admin registerAdmin(Admin admin) {
        return adminRepository.save(admin);
    }

    // Get admin by username
    public Admin getAdminByUsername(String username) {
        return adminRepository.findByUsername(username);
    }

    // Approve event request
    public String approveEvent(String eventId) {
        // Call the Event service using Feign Client to get the event details and update the status to 'approved'
        EventDTO event = eventClient.getEventById(eventId); // Fetch the event by ID
        if (event != null && !event.getStatus().equals("approved")) {
            event.setStatus("approved"); // Set the event status to approved
            eventClient.updateEventStatus(eventId, event); // Update the event status using Feign Client
            return "Event with ID: " + eventId + " has been approved";
        }
        return "Event with ID: " + eventId + " is either already approved or not found";
    }
    public Optional<Admin> getAdminFindById(String id) {
        return adminRepository.findById(id); // Fetch admin details using Feign Client
    }
}
