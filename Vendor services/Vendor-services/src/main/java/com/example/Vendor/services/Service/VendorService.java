package com.example.Vendor.services.Service;

import com.example.Vendor.services.Model.Payment;
import com.example.Vendor.services.Model.Vendor;
import com.example.Vendor.services.Repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VendorService { 
	@Autowired
	private VendorRepository vendorRepository;

    public ResponseEntity<Vendor> create(Vendor vendor) {
        Vendor savedVendor = vendorRepository.save(vendor);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedVendor);
    }

    public ResponseEntity<Vendor> getById(String id) {
        Optional<Vendor> vendorOpt = vendorRepository.findById(id);
        return vendorOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    public ResponseEntity<List<Vendor>> getAll() {
        List<Vendor> vendors = vendorRepository.findAll();
        return ResponseEntity.ok(vendors);
    }

    public ResponseEntity<Vendor> update(String id, Vendor vendor) {
        Optional<Vendor> existingVendorOpt = vendorRepository.findById(id);
        if (existingVendorOpt.isPresent()) {
            Vendor existingVendor = existingVendorOpt.get();
            existingVendor.setName(vendor.getName());
            existingVendor.setContactEmail(vendor.getContactEmail());
            existingVendor.setType(vendor.getType());
            existingVendor.setPayments(vendor.getPayments());
            existingVendor.setTotalAmount(vendor.getTotalAmount());
            existingVendor.setPendingAmount(vendor.getPendingAmount());
            existingVendor.setEventId(vendor.getEventId());

            Vendor updatedVendor = vendorRepository.save(existingVendor);
            return ResponseEntity.ok(updatedVendor);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    public ResponseEntity<Void> delete(String id) {
        Optional<Vendor> vendorOpt = vendorRepository.findById(id);
        if (vendorOpt.isPresent()) {
            vendorRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    public ResponseEntity<List<Vendor>> findByEventId(String eventId) {
        List<Vendor> vendors = vendorRepository.findByEventId(eventId);
        if (vendors.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(vendors);
    } 
    
    public ResponseEntity<List<Payment>> getPaymentsById(String id){
    	Optional<Vendor> optional = vendorRepository.findById(id); 
    	if(optional.isEmpty()) return ResponseEntity.notFound().build(); 
    	return ResponseEntity.ok(optional.get().getPayments());
    }
    public ResponseEntity<Vendor> addPaymentToVendor(String id, Payment payment) {  
        Optional<Vendor> optional = vendorRepository.findById(id); 
        if(optional.isEmpty()) return ResponseEntity.notFound().build(); 
        
        Vendor vendor = optional.get();
        List<Payment> payments = vendor.getPayments(); 
        payments.add(payment);  
        vendor.setPayments(payments);
        
        // Calculate total paid amount and update pending amount
        double totalPaidAmount = payments.stream()
            .mapToDouble(Payment::getAmount)
            .sum();
        vendor.setPendingAmount(vendor.getTotalAmount() - totalPaidAmount);
        
        return ResponseEntity.ok(vendorRepository.save(vendor));
    }  

    public ResponseEntity<Vendor> deletePayment(String vendorId, String paymentId) { 
        Optional<Vendor> optional = vendorRepository.findById(vendorId); 
        if(optional.isEmpty()) return ResponseEntity.noContent().build(); 
        
        Vendor vendor = optional.get();
        List<Payment> payments = vendor.getPayments(); 
        Payment paymentToRemove = null;
        
        for (Payment payment : payments) {
            if (payment.getId().equals(paymentId)) {
                paymentToRemove = payment;
                break;
            }
        } 
        
        payments.remove(paymentToRemove);
        vendor.setPayments(payments);
        
        // Calculate total paid amount and update pending amount
        double totalPaidAmount = payments.stream()
            .mapToDouble(Payment::getAmount)
            .sum();
        vendor.setPendingAmount(vendor.getTotalAmount() - totalPaidAmount);
        
        return ResponseEntity.ok(vendorRepository.save(vendor)); 
    }

    public Payment updatePayment(String vendorId, String paymentId, Payment updatedPayment) {
        Vendor vendor = vendorRepository.findById(vendorId)
            .orElseThrow(() -> new RuntimeException("Vendor not found"));
            
        List<Payment> payments = vendor.getPayments();
        int paymentIndex = -1;

        for (int i = 0; i < payments.size(); i++) {
            if (payments.get(i).getId().equals(paymentId)) {
                paymentIndex = i;
                break;
            }
        }
        
        if (paymentIndex == -1) {
            throw new RuntimeException("Payment not found");
        }
        
        updatedPayment.setId(paymentId);
        updatedPayment.setVendorId(vendorId);
        payments.set(paymentIndex, updatedPayment);
        vendor.setPayments(payments);
        
        // Calculate total paid amount and update pending amount
        double totalPaidAmount = payments.stream()
            .mapToDouble(Payment::getAmount)
            .sum();
        vendor.setPendingAmount(vendor.getTotalAmount() - totalPaidAmount);

        vendorRepository.save(vendor);
        return updatedPayment;
    }

    
}