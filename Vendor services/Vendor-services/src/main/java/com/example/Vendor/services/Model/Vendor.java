package com.example.Vendor.services.Model;

import com.example.Vendor.services.Enum.VendorType;
import jakarta.persistence.GeneratedValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document
public class Vendor { 
	@Id
	@GeneratedValue(generator = "custom-id-generator")
	@GenericGenerator(
			name = "custom-id-generator",
			strategy = "package com.example.Vendor.services.Generator.CustomIdGenerator;"
	)
	private String id; 
	private String name;
	private String contactEmail;
	private VendorType type;
	private List<Payment> payments;
	private Double totalAmount, pendingAmount;	
	private String eventId;
}