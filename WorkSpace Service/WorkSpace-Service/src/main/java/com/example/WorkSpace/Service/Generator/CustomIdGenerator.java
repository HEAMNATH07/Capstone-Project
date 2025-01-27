package com.example.WorkSpace.Service.Generator;


import com.example.WorkSpace.Service.Repository.WorkspaceRepository;
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
        WorkspaceRepository workspaceRepository = applicationContext.getBean(WorkspaceRepository.class);
        int count = (int) workspaceRepository.count();
        return "WS" + (count + 1) + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
    }
}