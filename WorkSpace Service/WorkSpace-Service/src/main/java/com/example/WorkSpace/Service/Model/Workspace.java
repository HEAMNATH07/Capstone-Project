package com.example.WorkSpace.Service.Model;

import jakarta.persistence.GeneratedValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "workspaces")
public class Workspace {
    @Id
    @GeneratedValue(generator = "custom-id-generator")
    @GenericGenerator(
            name = "custom-id-generator",
            strategy = "package com.example.WorkSpace.service.Generator.CustomIdGenerator;"
    )
    private String id;
    private String floor;
    private String room;
    private String seatNumber;
    private boolean isBooked;
    private String projectId;
    private String employeeId;
    private LocalDateTime bookTime;
    private int duration;

    public boolean isBookedForMoreThan24Hours() {
        return bookTime != null && bookTime.plusHours(24).isBefore(LocalDateTime.now());
    }
}