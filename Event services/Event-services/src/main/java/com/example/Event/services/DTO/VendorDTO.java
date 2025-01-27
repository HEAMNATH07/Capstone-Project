package com.example.Event.services.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VendorDTO {
    private String id;
    private String name;
    private String contactEmail;
    private VendorType type;
    private List<String> payments;
    private Double totalAmount;
    private Double pendingAmount;
    private String eventId;



}
