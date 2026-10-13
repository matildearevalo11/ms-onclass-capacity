package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.handler.ICapacityDeleteHandler;
import com.pragma.powerup.domain.api.ICapacityDeleteServicePort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CapacityDeleteHandler implements ICapacityDeleteHandler {
    private final ICapacityDeleteServicePort servicePort;

    @Override
    @Transactional
    public Mono<Void> deleteCapacities(List<Long> ids) {
        return servicePort.deleteCapacities(ids);
    }
}
