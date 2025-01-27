package com.example.WorkSpace.Service.Service;

import com.example.WorkSpace.Service.Config.EmployeeServiceClient;
import com.example.WorkSpace.Service.DTO.EmployeeDTO;
import com.example.WorkSpace.Service.Model.Workspace;
import com.example.WorkSpace.Service.Repository.WorkspaceRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
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
import java.util.List;

@Service
public class WorkspaceService {

    @Autowired
    private WorkspaceRepository workspaceRepository;

    @Autowired
    private EmployeeServiceClient employeeServiceClient;

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${workspace.booking.queue}")
    private String queueName;

    // Create a new workspace
    public Workspace createWorkspace(String floor, String room, String seatNumber, String projectId) {
        Workspace workspace = new Workspace();

        // Set values based on user input
        workspace.setFloor(floor);
        workspace.setRoom(room);
        workspace.setSeatNumber(seatNumber);
        workspace.setProjectId(projectId);

        // Set default values for other fields
        workspace.setBooked(false); // Default to unbooked
        workspace.setEmployeeId(null);
        workspace.setBookTime(null);
        workspace.setDuration(0);

        // Save the workspace
        return workspaceRepository.save(workspace);
    }

    // Retrieve a workspace by ID
    public Workspace getWorkspaceById(String id) {
        return workspaceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Workspace not found with ID: " + id));
    }

    // Retrieve all workspaces
    public List<Workspace> getAllWorkspaces() {
        return workspaceRepository.findAll();
    }

    // Update a workspace
    public Workspace updateWorkspace(String id, Workspace workspaceDetails) {
        Workspace existingWorkspace = getWorkspaceById(id);

        existingWorkspace.setFloor(workspaceDetails.getFloor());
        existingWorkspace.setRoom(workspaceDetails.getRoom());
        existingWorkspace.setSeatNumber(workspaceDetails.getSeatNumber());
        existingWorkspace.setProjectId(workspaceDetails.getProjectId());
        existingWorkspace.setBooked(workspaceDetails.isBooked());
        existingWorkspace.setEmployeeId(workspaceDetails.getEmployeeId());

        return workspaceRepository.save(existingWorkspace);
    }

    // Delete a workspace
    public void deleteWorkspace(String id) {
        Workspace workspace = getWorkspaceById(id);
        workspaceRepository.delete(workspace);
    }

    public String bookWorkspace(String floor, String room, String seatNumber, String employeeId) {
        Workspace workspace = workspaceRepository.findByFloorAndRoomAndSeatNumber(floor, room, seatNumber);

        if (workspace == null) {
            throw new RuntimeException("Workspace not found");
        }

        if (workspace.isBooked()) {
            throw new RuntimeException("Workspace is already booked");
        }

        EmployeeDTO employee = employeeServiceClient.getEmployeeById(employeeId);

        if (workspace.getProjectId() != null) {
            if (employee.getProjectId() == null || !workspace.getProjectId().equals(employee.getProjectId())) {
                throw new RuntimeException("You can only book workspaces assigned to your project.");
            }
        } else {
            if (employee.getProjectId() != null) {
                throw new RuntimeException("General workspaces are for employees without projects.");
            }
        }

        workspace.setBooked(true);
        workspace.setEmployeeId(employeeId);
        workspaceRepository.save(workspace);

        try {
            String qrData = "Workspace ID: " + workspace.getId() + "\nFloor: " + workspace.getFloor() + "\nRoom: " + workspace.getRoom() + "\nSeatNumber: " + workspace.getSeatNumber();
            byte[] qrCode = generateQRCode(qrData);
            sendWorkspaceAllocationEmail(employee.getEmail(), workspace, qrCode);
        } catch (Exception e) {
            throw new RuntimeException("Error generating QR code or sending email", e);
        }

        return "Workspace booked successfully!";
    }

    // Generate QR code
    private byte[] generateQRCode(String data) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, 200, 200);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

        return outputStream.toByteArray();
    }

    // Send workspace allocation email with QR code
    public void sendWorkspaceAllocationEmail(String email, Workspace workspace, byte[] qrCode) throws MessagingException, IOException, IOException {
        // Load the index.html template
        Resource resource = new ClassPathResource("index.html");
        String htmlTemplate = new String(Files.readAllBytes(resource.getFile().toPath()));

        // Replace placeholders in the HTML with actual data
        htmlTemplate = htmlTemplate.replace("[Workspace ID]", workspace.getId())
                .replace("[Floor]", workspace.getFloor())
                .replace("[Room]", workspace.getRoom())
                .replace("[Seat Number]", workspace.getSeatNumber());

        // Create the MIME message
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        // Set recipient and subject
        helper.setTo(email);
        helper.setSubject("Workspace Allocation Confirmation");

        // Set the HTML content
        helper.setText(htmlTemplate, true);

        // Attach the QR code as an image
        ByteArrayDataSource qrCodeAttachment = new ByteArrayDataSource(qrCode, "image/png");
        helper.addAttachment("WorkspaceQR.png", qrCodeAttachment);

        // Send the email
        javaMailSender.send(mimeMessage);
    }

    // Scheduled task to unbook workspaces after a certain period (if required, for example, 24 hours)
    @Scheduled(cron = "0 0 0 * * ?") // Example: Run at midnight every day
    public void unbookWorkspacesAfterPeriod() {
        List<Workspace> bookedWorkspaces = workspaceRepository.findByBooked(true);
        for (Workspace workspace : bookedWorkspaces) {
            // Your logic to determine if workspace booking should expire
            unbookWorkspace(workspace.getId());
        }
    }

    // Unbook a workspace
    public String unbookWorkspace(String workspaceId) {
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new RuntimeException("Workspace not found"));

        workspace.setBooked(false);
        workspace.setEmployeeId(null);
        workspaceRepository.save(workspace);

        return "Workspace unbooked successfully!";
    }
}
