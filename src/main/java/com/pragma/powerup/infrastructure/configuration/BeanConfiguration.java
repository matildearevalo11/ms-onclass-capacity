package com.pragma.powerup.infrastructure.configuration;

import com.pragma.powerup.domain.api.ICapacityServicePort;
import com.pragma.powerup.domain.spi.ICapacityPersistencePort;
import com.pragma.powerup.domain.spi.ITechnologyAssociationPort;
import com.pragma.powerup.domain.usecase.CapacityUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {
    @Bean
    ICapacityServicePort capacityServicePort(ICapacityPersistencePort persistencePort, ITechnologyAssociationPort technologyPort) {
        return new CapacityUseCase(persistencePort, technologyPort);
    }
}
