package com.pragma.powerup.infrastructure.configuration;

import com.pragma.powerup.domain.api.ICapacityServicePort;
import com.pragma.powerup.domain.api.ICapacityListServicePort;
import com.pragma.powerup.domain.api.ICapacityDeleteServicePort;
import com.pragma.powerup.domain.usecase.CapacityDeleteUseCase;
import com.pragma.powerup.domain.spi.ICapacityPersistencePort;
import com.pragma.powerup.domain.spi.ICapacityQueryPort;
import com.pragma.powerup.domain.spi.ITechnologyAssociationPort;
import com.pragma.powerup.domain.spi.ITechnologyQueryPort;
import com.pragma.powerup.domain.usecase.CapacityListUseCase;
import com.pragma.powerup.domain.usecase.CapacityUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {
    @Bean
    ICapacityDeleteServicePort capacityDeleteServicePort(ICapacityPersistencePort capacityPort,
            ITechnologyAssociationPort technologyPort) {
        return new CapacityDeleteUseCase(capacityPort, technologyPort);
    }
    @Bean
    ICapacityListServicePort capacityListServicePort(ICapacityQueryPort capacityPort,
            ITechnologyQueryPort technologyPort) {
        return new CapacityListUseCase(capacityPort, technologyPort);
    }

    @Bean
    ICapacityServicePort capacityServicePort(ICapacityPersistencePort persistencePort, ITechnologyAssociationPort technologyPort) {
        return new CapacityUseCase(persistencePort, technologyPort);
    }
}
