package com.example.Event.services.Repository;

import com.example.Event.services.Model.Event;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends MongoRepository<Event, String> {

    List<Event> findByStatus(String status);

    List<Event> findByBookedEmployeesContaining(String employeeId);
}
