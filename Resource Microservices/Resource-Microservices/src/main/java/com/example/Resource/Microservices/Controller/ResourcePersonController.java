package com.example.Resource.Microservices.Controller;

import com.example.Resource.Microservices.Model.ResourcePerson;
import com.example.Resource.Microservices.Service.ResourcePersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/resource-persons")
public class ResourcePersonController {


    @Autowired
    private ResourcePersonService resourcePersonService;

    // Create a new resource person
    @PostMapping
    public ResponseEntity<ResourcePerson> createResourcePerson(@RequestBody ResourcePerson resourcePerson) {
        ResourcePerson createdResourcePerson = resourcePersonService.createResourcePerson(resourcePerson);
        return ResponseEntity.ok(createdResourcePerson);
    }

    // Get all resource persons
    @GetMapping
    public ResponseEntity<List<ResourcePerson>> getAllResourcePersons() {
        List<ResourcePerson> resourcePersons = resourcePersonService.getAllResourcePersons();
        return ResponseEntity.ok(resourcePersons);
    }

    // Get resource persons by department (e.g., Cleaner, Security)
    @GetMapping("/department/{department}")
    public ResponseEntity<List<ResourcePerson>> getResourcePersonsByDepartment(@PathVariable String department) {
        List<ResourcePerson> resourcePersons = resourcePersonService.getResourcePersonsByDepartment(department);
        return ResponseEntity.ok(resourcePersons);
    }

    // Get resource persons by availability status (true = available, false = unavailable)
    @GetMapping("/availability/{isAvailable}")
    public ResponseEntity<List<ResourcePerson>> getResourcePersonsByAvailability(@PathVariable boolean isAvailable) {
        List<ResourcePerson> resourcePersons = resourcePersonService.getResourcePersonsByAvailability(isAvailable);
        return ResponseEntity.ok(resourcePersons);
    }

    // Update resource person's availability status
    @PutMapping("/{id}/availability/{isAvailable}")
    public ResponseEntity<ResourcePerson> updateResourcePersonStatus(@PathVariable String id, @PathVariable boolean isAvailable) {
        ResourcePerson updatedResourcePerson = resourcePersonService.updateResourcePersonStatus(id, isAvailable);
        return ResponseEntity.ok(updatedResourcePerson);
    }

    // Remove a resource person
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeResourcePerson(@PathVariable String id) {
        resourcePersonService.removeResourcePerson(id);
        return ResponseEntity.noContent().build();
    }
}


