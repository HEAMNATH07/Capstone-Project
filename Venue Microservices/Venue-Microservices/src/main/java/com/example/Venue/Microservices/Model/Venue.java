package com.example.Venue.Microservices.Model;

import jakarta.persistence.GeneratedValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "venues")
public class Venue {
    @Id
    @GeneratedValue(generator = "custom-id-generator")
    @GenericGenerator(
            name = "custom-id-generator",
            strategy = "package com.example.Vendor.Microservices.Generator.CustomIdGenerator;"
    )
    private String id;
    private String name;
    private String location;
    private int capacity;
    private boolean isAvailable;
}
