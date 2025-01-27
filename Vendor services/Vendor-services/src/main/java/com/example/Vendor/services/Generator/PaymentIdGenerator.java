package com.example.Vendor.services.Generator;

import com.example.Vendor.services.Repository.VendorRepository;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.UUID;

@Component // Register this class as a Spring bean
public class PaymentIdGenerator implements IdentifierGenerator, ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext context) {
        applicationContext = context;
    }

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) {
        VendorRepository vendorRepository = applicationContext.getBean(VendorRepository.class);
        int count = (int) vendorRepository.count();
        return "PAY" + (count + 1) + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
    }
}