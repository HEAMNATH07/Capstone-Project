package com.example.Resource.Microservices.Service;

import com.example.Resource.Microservices.Model.ResourcePerson;
import com.example.Resource.Microservices.Repository.ResourcePersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResourcePersonService {

    @Autowired
    private ResourcePersonRepository resourcePersonRepository;

    // Create a new resource person
    public ResourcePerson createResourcePerson(ResourcePerson resourcePerson) {
        return resourcePersonRepository.save(resourcePerson);
    }

    // Get all resource persons
    public List<ResourcePerson> getAllResourcePersons() {
        return resourcePersonRepository.findAll();
    }

    // Get resource persons by department (e.g., Cleaner, Security)
    public List<ResourcePerson> getResourcePersonsByDepartment(String department) {
        return resourcePersonRepository.findByDepartment(department);
    }

    // Get resource persons by status
    public List<ResourcePerson> getResourcePersonsByAvailability(boolean isAvailable) {
        return resourcePersonRepository.findByIsAvailable(isAvailable);
    }

    // Update resource person's status
    public ResourcePerson updateResourcePersonStatus(String id, boolean isAvaiable) {
        ResourcePerson resourcePerson = resourcePersonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resource person not found with ID: " + id));
        resourcePerson.setAvailable(isAvaiable);
        return resourcePersonRepository.save(resourcePerson);
    }

    // Remove a resource person
    public void removeResourcePerson(String id) {
        ResourcePerson resourcePerson = resourcePersonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resource person not found with ID: " + id));
        resourcePersonRepository.delete(resourcePerson);
    }
}