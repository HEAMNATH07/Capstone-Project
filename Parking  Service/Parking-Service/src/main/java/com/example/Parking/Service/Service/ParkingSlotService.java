package com.example.Parking.Service.Service;

import com.example.Parking.Service.Config.EmployeeServiceClient;
import com.example.Parking.Service.DTO.EmployeeDTO;
import com.example.Parking.Service.Enum.VehicleType;
import com.example.Parking.Service.Model.ParkingSlot;
import com.example.Parking.Service.Repository.ParkingSlotRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class ParkingSlotService {

    @Autowired
    private ParkingSlotRepository parkingSlotRepository;

    @Autowired
    private EmployeeServiceClient employeeServiceClient;

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${parking.booking.queue}")
    private String queueName;

    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);

    public ParkingSlot saveParkingSlot(String floor, String section, String slotNumber, VehicleType vehicleType) {
        ParkingSlot parkingSlot = new ParkingSlot();

        // Set values based on user input
        parkingSlot.setFloor(floor);
        parkingSlot.setSection(section);
        parkingSlot.setSlotNumber(slotNumber);
        parkingSlot.setVehicleType(vehicleType);

        // Set default values for other fields
        parkingSlot.setBooked(false); // Default to unbooked
        parkingSlot.setEmployeeId(null); // Default to null, no employee assigned
        parkingSlot.setBookTime(null); // Default to no booking time
        parkingSlot.setDuration(0); // Default to 0 duration

        // Save the parking slot
        return parkingSlotRepository.save(parkingSlot);
    }

    // Get all parking slots
    public List<ParkingSlot> getAllParkingSlots() {
        return parkingSlotRepository.findAll();
    }

    // Get parking slot by ID
    public Optional<ParkingSlot> getParkingSlotById(String id) {
        return parkingSlotRepository.findById(id);
    }

    // Get parking slots by floor
    public List<ParkingSlot> getParkingSlotsByFloor(String floor) {
        return parkingSlotRepository.findByFloor(floor);
    }

    // Get parking slots by vehicle type
    public List<ParkingSlot> getParkingSlotsByVehicleType(String vehicleType) {
        return parkingSlotRepository.findByVehicleType(vehicleType);
    }

    // Get parking slots by booking status
    public List<ParkingSlot> getParkingSlotsByBookingStatus(boolean isBooked) {
        return parkingSlotRepository.findByIsBooked(isBooked);
    }
    public List<ParkingSlot> getParkingSlotsByFloorAndSection(String floor, String section) {
        return parkingSlotRepository.findByFloorAndSection(floor, section);
    }


    // Delete a parking slot
    public void deleteParkingSlot(String id) {
        parkingSlotRepository.deleteById(id);
    }

    // Book a parking slot
    public String bookParkingSlot(String floor, String section, String slotNumber, String employeeId, VehicleType vehicleType, int duration) {
        ParkingSlot parkingSlot = parkingSlotRepository.findByFloorAndSectionAndSlotNumber(floor, section, slotNumber);

        if (parkingSlot == null) {
            throw new RuntimeException("Parking slot not found");
        }

        if (parkingSlot.isBooked()) {
            throw new RuntimeException("Parking slot is already booked");
        }

        if (parkingSlot.getVehicleType() != vehicleType) {
            throw new RuntimeException("This parking slot is not suitable for vehicle type: " + vehicleType);
        }

        EmployeeDTO employee = employeeServiceClient.getEmployeeById(employeeId);

        parkingSlot.setBooked(true);
        parkingSlot.setEmployeeId(employeeId);
        parkingSlot.setBookTime(LocalDateTime.now());
        parkingSlot.setDuration(duration); // Set the duration for the booking
        parkingSlotRepository.save(parkingSlot);

        try {
            String qrData = "Parking Slot ID: " + parkingSlot.getId() + "\nFloor: " + parkingSlot.getFloor() +
                    "\nSection: " + parkingSlot.getSection() + "\nSlot Number: " + parkingSlot.getSlotNumber();
            byte[] qrCode = generateQRCode(qrData);
            sendParkingAllocationEmail(employee.getEmail(), parkingSlot, qrCode);
        } catch (Exception e) {
            throw new RuntimeException("Error generating QR code or sending email", e);
        }

        scheduleUnbooking(parkingSlot);
        return "Parking slot booked successfully!";
    }

    // Unbook a parking slot
    public String unbookParkingSlot(String parkingSlotId) {
        ParkingSlot parkingSlot = parkingSlotRepository.findById(parkingSlotId)
                .orElseThrow(() -> new RuntimeException("Parking slot not found"));

        parkingSlot.setBooked(false);
        parkingSlot.setEmployeeId(null);
        parkingSlot.setBookTime(null);
        parkingSlot.setDuration(0);
        parkingSlotRepository.save(parkingSlot);

        return "Parking slot unbooked successfully!";
    }

    // Scheduled task to unbook parking slots after 24 hours
    @Scheduled(cron = "0 0 0 * * ?") // Run at midnight every day
    public void unbookParkingSlotsAfter24Hours() {
        List<ParkingSlot> bookedSlots = parkingSlotRepository.findByIsBooked(true);
        for (ParkingSlot slot : bookedSlots) {
            if (slot.getBookTime().plusHours(24).isBefore(LocalDateTime.now())) {
                unbookParkingSlot(slot.getId());
            }
        }
    }

    // Generate QR code
    private byte[] generateQRCode(String data) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, 200, 200);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

        return outputStream.toByteArray();
    }

    // Send parking allocation email with QR code
    public void sendParkingAllocationEmail(String email, ParkingSlot parkingSlot, byte[] qrCode) throws MessagingException, IOException, IOException {
        // Load the index.html template
        Resource resource = new ClassPathResource("index.html");
        String htmlTemplate = new String(Files.readAllBytes(resource.getFile().toPath()));

        // Replace placeholders in the HTML with actual data
        htmlTemplate = htmlTemplate.replace("[Parking Slot ID]", parkingSlot.getId())
                .replace("[Floor]", parkingSlot.getFloor())
                .replace("[Section]", parkingSlot.getSection())
                .replace("[Slot Number]", parkingSlot.getSlotNumber());

        // Create the MIME message
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        // Set recipient and subject
        helper.setTo(email);
        helper.setSubject("Parking Slot Allocation Confirmation");

        // Set the HTML content
        helper.setText(htmlTemplate, true);

        // Attach the QR code as an image
        ByteArrayDataSource qrCodeAttachment = new ByteArrayDataSource(qrCode, "image/png");
        helper.addAttachment("ParkingSlotQR.png", qrCodeAttachment);

        // Send the email
        javaMailSender.send(mimeMessage);
    }

    // Schedule unbooking for a specific parking slot based on its duration
    public void scheduleUnbooking(ParkingSlot parkingSlot) {
        long delay = calculateDelay(parkingSlot.getBookTime(), parkingSlot.getDuration());
        if (delay > 0) {
            executorService.schedule(() -> unbookParkingSlot(parkingSlot.getId()), delay, TimeUnit.SECONDS);
            System.out.println("Unbooking scheduled for parking slot: " + parkingSlot.getId() + " after " + delay + " seconds");
        }
    }

    // Calculate delay in seconds based on booking time and duration
    private long calculateDelay(LocalDateTime bookTime, int duration) {
        LocalDateTime expiryTime = bookTime.plusHours(duration);
        return Duration.between(LocalDateTime.now(), expiryTime).getSeconds();
    }

    // Initialize scheduling for all booked slots (optional, for application startup)
    @PostConstruct
    public void initializeScheduledUnbooking() {
        List<ParkingSlot> bookedSlots = parkingSlotRepository.findByIsBooked(true);
        for (ParkingSlot slot : bookedSlots) {
            scheduleUnbooking(slot);
        }
    }
}
