package com.example.Parking.Service.Repository;


import com.example.Parking.Service.Enum.VehicleType;
import com.example.Parking.Service.Model.ParkingSlot;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ParkingSlotRepository extends MongoRepository<ParkingSlot, String> {
    List<ParkingSlot> findByVehicleTypeAndIsBooked(VehicleType vehicleType, boolean isBooked);

    ParkingSlot findByFloorAndSectionAndSlotNumber(String floor, String section, String slotNumber);

    List<ParkingSlot> findByFloor(String floor);

    List<ParkingSlot> findByVehicleType(String vehicleType);

    List<ParkingSlot> findByIsBooked(boolean isBooked);

    List<ParkingSlot> findByFloorAndSection(String floor, String section);
}
