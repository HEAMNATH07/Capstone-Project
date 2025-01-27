package com.example.Parking.Service.Generator;

import com.example.Parking.Service.Repository.ParkingSlotRepository;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.UUID;

@Component // Register this class as a Spring bean
public class CustomIdGenerator implements IdentifierGenerator, ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext context) {
        applicationContext = context;
    }

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) {
        ParkingSlotRepository parkingSlotRepository = applicationContext.getBean(ParkingSlotRepository.class);
        int count = (int) parkingSlotRepository.count();
        return "PS" + (count + 1) + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
    }
}