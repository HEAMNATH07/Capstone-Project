package com.example.Resource.Microservices.Model;

import jakarta.persistence.GeneratedValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "resource_persons")
public class ResourcePerson {
    @Id
    @GeneratedValue(generator = "custom-id-generator")
    @GenericGenerator(
            name = "custom-id-generator",
            strategy = "package com.example.Resource.Microservices.Generator.CustomIdGenerator;"
    )
    private String id;
    private String name;
    private String contactInfo;
    private boolean isAvailable;
    private String department;

}