package com.example.Vendor.services.Model;

import com.example.Vendor.services.Enum.PaymentStatus;
import com.example.Vendor.services.Enum.PaymentType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
	@Id
	@GeneratedValue(generator = "custom-id-generator")
	@GenericGenerator(
			name = "payment-id-generator",
			strategy = "package com.example.Vendor.services.Generator.PaymentIdGenerator;"
	)
	private String id;   
	private String vendorId;
	private Double amount;
	private String referenceNumber;

	@Temporal(TemporalType.DATE)
	@DateTimeFormat(style = "dd-MM-yyyy")
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="dd-MM-yyyy")
	Date paymentDate;
	
	PaymentStatus paymentStatus;
	PaymentType paymentType;
}