package com.example.Event.services.Service;

import com.example.Event.services.Config.*;
import com.example.Event.services.DTO.*;
import com.example.Event.services.Model.Event;
import com.example.Event.services.Repository.EventRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.util.*;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private AdminFeignClient adminFeignClient;

    @Autowired
    private EmployeeFeignClient employeeFeignClient;

    @Autowired
    private ResourcePersonFeignClient resourcePersonFeignClient;

    @Autowired
    private VendorFeignClient vendorFeignClient;

    @Autowired
    private VenueFeignClient venueFeignClient;

    @Autowired
    private JavaMailSender javaMailSender;

    // Create a new event
    public Event createEvent(Event event) {
        event.setStatus("Pending");
        event.setBookedEmployees(new ArrayList<>()); // Initialize the list of booked employees
        Event createdEvent = eventRepository.save(event);
        return createdEvent;
    }

    // Get event details
    public Event getEvent(String eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with ID: " + eventId));
    }

    // Approve event
    public Event approveEvent(String eventId) {
        Event event = getEvent(eventId);
        event.setStatus("Approved");
        return eventRepository.save(event);
    }

    // Add vendor to the event
    public Event addVendor(String eventId, String vendorId) {
        Event event = getEvent(eventId);
        event.getVendorIds().add(vendorId);
        return eventRepository.save(event);
    }
    public List<Event> getEventsByStatus(String status) {
        List<Event> events = eventRepository.findByStatus(status);
        if (events.isEmpty()) {
            throw new RuntimeException("No events found with the given status: " + status);
        }
        return events;
    }
    public List<Event> getEventsByEmployeeId(String employeeId) {
        List<Event> events = eventRepository.findByBookedEmployeesContaining(employeeId);
        if (events.isEmpty()) {
            throw new RuntimeException("No events found for the given employee ID: " + employeeId);
        }
        return events;
    }

    // Request resource person for the event (e.g., cleaner, security)
    public Event requestResourcePerson(String eventId, String resourcePersonId) {
        Event event = getEvent(eventId);
        if (!event.getResourcePersonIds().contains(resourcePersonId)) {
            event.getResourcePersonIds().add(resourcePersonId);
            event.getResourcePersonRequests().put(resourcePersonId, "Pending"); // Mark as pending
            return eventRepository.save(event);
        } else {
            throw new RuntimeException("Resource person already requested for this event");
        }
    }

    // Approve or reject resource person request
    public Event approveResourcePerson(String eventId, String resourcePersonId, boolean isApproved) {
        Event event = getEvent(eventId);
        Map<String, String> resourcePersonRequests = event.getResourcePersonRequests();

        if (resourcePersonRequests.containsKey(resourcePersonId)) {
            if (!"Pending".equals(resourcePersonRequests.get(resourcePersonId))) {
                throw new RuntimeException("Request has already been processed");
            }
            resourcePersonRequests.put(resourcePersonId, isApproved ? "Approved" : "Rejected");
            return eventRepository.save(event);
        } else {
            throw new RuntimeException("Resource person request not found for this event");
        }
    }

    // Book seats for the event
    @Transactional
    public Event bookSeats(String eventId, int seats, String userEmail, String employeeId) throws Exception {
        Event event = getEvent(eventId);

        // Check if the employee has already booked a seat for the event
        if (event.getBookedEmployees().contains(employeeId)) {
            throw new RuntimeException("You have already booked a seat for this event.");
        }

        // Check if enough seats are available
        int availableSeats = event.getTotalSeats() - event.getSeatsBooked();
        if (availableSeats < seats) {
            throw new RuntimeException("Not enough seats available.");
        }

        event.setSeatsBooked(event.getSeatsBooked() + seats);
        event.getBookedEmployees().add(employeeId); // Mark employee as having booked a seat

        // Save updated event
        Event savedEvent = eventRepository.save(event);

        // Generate QR code for the booking
        String qrCodeData = "Event ID: " + eventId + "\nSeats Booked: " + seats;
        byte[] qrCode = generateQRCode(qrCodeData);

        // Send the booking confirmation email with the QR code
        sendEventBookingEmail(userEmail, savedEvent, qrCode);

        return savedEvent;
    }

    // Generate QR Code from data
    private byte[] generateQRCode(String data) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, 200, 200);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

        return outputStream.toByteArray();
    }

    // Send email notification with QR code attached
    private void sendEventBookingEmail(String userEmail, Event event, byte[] qrCode) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setTo(userEmail);
        helper.setSubject("Event Booking Confirmation");
        helper.setText("Dear User,\n\n"
                + "Your booking for the event \"" + event.getName() + "\" with ID: \"" + event.getId() + "\" has been successfully confirmed.\n"
                + "Seats booked: " + event.getSeatsBooked() + "\n"
                + "Total Seats: " + event.getTotalSeats() + "\n\n"
                + "Please find the attached QR code for your records.\n\n"
                + "Best regards,\nEvent Management Team");

        // Create ByteArrayDataSource from the generated QR code
        ByteArrayDataSource qrCodeAttachment = new ByteArrayDataSource(qrCode, "image/png");
        helper.addAttachment("EventQRCode.png", qrCodeAttachment);

        // Send the email
        javaMailSender.send(mimeMessage);
    }

    // Check venue availability before booking the event
    public boolean isVenueAvailable(String venueId, int seatsRequired) {
        VenueDTO venue = venueFeignClient.getVenueById(venueId);
        if (venue != null) {
            return venue.getCapacity() >= seatsRequired && venue.isAvailable();
        } else {
            throw new RuntimeException("Venue not found");
        }
    }

    // Get all events (for admin or managers)
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    // Delete an event by ID (admin functionality)
    @Transactional
    public void deleteEvent(String eventId) {
        Event event = getEvent(eventId);
        eventRepository.delete(event);
    }

    // Update event details
    public Event updateEvent(String eventId, Event eventDetails) {
        Event existingEvent = getEvent(eventId);
        existingEvent.setName(eventDetails.getName());
        existingEvent.setDescription(eventDetails.getDescription());
        existingEvent.setVenueId(eventDetails.getVenueId());
        existingEvent.setTotalSeats(eventDetails.getTotalSeats());
        existingEvent.setEventDate(eventDetails.getEventDate());
        return eventRepository.save(existingEvent);
    }
    // Send event approval request to Admin service
    public Optional<AdminDTO> sendEventApprovalRequest(String adminId) {
        return adminFeignClient.getAdminDetails(adminId);
    }

    // Fetch employee details
    public EmployeeDTO getEmployeeDetails(String employeeId) {
        return employeeFeignClient.getEmployeeById(employeeId);
    }

    // Fetch vendor details
    public VendorDTO getVendorDetails(String vendorId) {
        return vendorFeignClient.getById(vendorId);
    }

    // Fetch resource person details
    public ResourcePersonDTO getResourcePersonDetails(String resourcePersonId) {
        return resourcePersonFeignClient.getResourcePersonById(resourcePersonId);
    }

    // Fetch venue details
    public VenueDTO getVenueDetails(String venueId) {
        return venueFeignClient.getVenueById(venueId);
    }
}

