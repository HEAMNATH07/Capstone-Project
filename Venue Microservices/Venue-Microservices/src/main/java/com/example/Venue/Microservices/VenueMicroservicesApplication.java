package com.example.Venue.Microservices;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
public class VenueMicroservicesApplication {

	public static void main(String[] args) {
		SpringApplication.run(VenueMicroservicesApplication.class, args);
	}

}
