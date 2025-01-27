package com.example.Parking.Service.Controller;

import com.example.Parking.Service.Enum.VehicleType;
import com.example.Parking.Service.Model.ParkingSlot;
import com.example.Parking.Service.Service.ParkingSlotService;
import com.example.Parking.Service.DTO.EmployeeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/parking-slots")
public class ParkingSlotController {

    @Autowired
    private ParkingSlotService parkingSlotService;

    // Create or Update a Parking Slot
    @PostMapping("/create")
    public ResponseEntity<ParkingSlot> createOrUpdateParkingSlot(@RequestBody ParkingSlot parkingSlot) {
        ParkingSlot savedParkingSlot = parkingSlotService.saveParkingSlot(parkingSlot);
        return new ResponseEntity<>(savedParkingSlot, HttpStatus.CREATED);
    }

    // Get all parking slots
    @GetMapping("/all")
    public ResponseEntity<List<ParkingSlot>> getAllParkingSlots() {
        List<ParkingSlot> parkingSlots = parkingSlotService.getAllParkingSlots();
        return new ResponseEntity<>(parkingSlots, HttpStatus.OK);
    }

    // Get parking slot by ID
    @GetMapping("/{id}")
    public ResponseEntity<ParkingSlot> getParkingSlotById(@PathVariable String id) {
        Optional<ParkingSlot> parkingSlot = parkingSlotService.getParkingSlotById(id);
        return parkingSlot.map(slot -> new ResponseEntity<>(slot, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Get parking slots by floor
    @GetMapping("/floor/{floor}")
    public ResponseEntity<List<ParkingSlot>> getParkingSlotsByFloor(@PathVariable String floor) {
        List<ParkingSlot> parkingSlots = parkingSlotService.getParkingSlotsByFloor(floor);
        return new ResponseEntity<>(parkingSlots, HttpStatus.OK);
    }

    // Get parking slots by vehicle type
    @GetMapping("/vehicle-type/{vehicleType}")
    public ResponseEntity<List<ParkingSlot>> getParkingSlotsByVehicleType(@PathVariable String vehicleType) {
        try {
            VehicleType type = VehicleType.valueOf(vehicleType.toUpperCase());
            List<ParkingSlot> parkingSlots = parkingSlotService.getParkingSlotsByVehicleType(type.toString());
            return new ResponseEntity<>(parkingSlots, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/by-floor-and-section")
    public ResponseEntity<List<ParkingSlot>> getParkingSlotsByFloorAndSection(
            @RequestParam String floor,
            @RequestParam String section) {
        List<ParkingSlot> parkingSlots = parkingSlotService.getParkingSlotsByFloorAndSection(floor, section);
        return ResponseEntity.ok(parkingSlots);
    }

    // Get parking slots by booking status
    @GetMapping("/booking-status/{status}")
    public ResponseEntity<List<ParkingSlot>> getParkingSlotsByBookingStatus(@PathVariable boolean status) {
        List<ParkingSlot> parkingSlots = parkingSlotService.getParkingSlotsByBookingStatus(status);
        return new ResponseEntity<>(parkingSlots, HttpStatus.OK);
    }

    // Delete a parking slot by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParkingSlot(@PathVariable String id) {
        parkingSlotService.deleteParkingSlot(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Book a parking slot
    @PostMapping("/book")
    public ResponseEntity<String> bookParkingSlot(@RequestParam String floor,
                                                  @RequestParam String section,
                                                  @RequestParam String slotNumber,
                                                  @RequestParam String employeeId,
                                                  @RequestParam String vehicleType,
                                                  @RequestParam int duration) {
        try {
            VehicleType type = VehicleType.valueOf(vehicleType.toUpperCase());
            String response = parkingSlotService.bookParkingSlot(floor, section, slotNumber, employeeId, type, duration);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Invalid vehicle type", HttpStatus.BAD_REQUEST);
        }
    }

    // Unbook a parking slot
    @PostMapping("/unbook")
    public ResponseEntity<String> unbookParkingSlot(@RequestParam String parkingSlotId) {
        String response = parkingSlotService.unbookParkingSlot(parkingSlotId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
