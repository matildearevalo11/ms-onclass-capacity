package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.request.CapacityRequestDto;
import com.pragma.powerup.application.dto.response.CapacityResponseDto;
import com.pragma.powerup.application.handler.ICapacityHandler;
import com.pragma.powerup.application.mapper.CapacityMapper;
import com.pragma.powerup.domain.api.ICapacityServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CapacityHandler implements ICapacityHandler {
    private final ICapacityServicePort servicePort;
    private final CapacityMapper mapper;

    @Override
    @Transactional
    public Mono<CapacityResponseDto> createCapacity(CapacityRequestDto request) {
        return servicePort.createCapacity(mapper.toModel(request)).map(mapper::toResponse);
    }
}
