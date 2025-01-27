package com.example.Vendor.services.Repository;

import com.example.Vendor.services.Model.Payment;
import com.example.Vendor.services.Model.Vendor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VendorRepository extends MongoRepository<Vendor, String> {
	public List<Vendor> findByEventId(String eventId);
	public List<Payment> findPaymentsById(String id);
}