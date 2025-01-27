package com.example.Resource.Microservices.Repository;

import com.example.Resource.Microservices.Model.ResourcePerson;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourcePersonRepository extends MongoRepository<ResourcePerson, String> {
    List<ResourcePerson> findByDepartment(String department);

    List<ResourcePerson> findByIsAvailable(boolean isAvailable);
}