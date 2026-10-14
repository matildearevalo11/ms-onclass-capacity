package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.request.CapacityPageRequestDto;
import com.pragma.powerup.application.dto.response.CapacityPageResponseDto;
import com.pragma.powerup.application.handler.ICapacityListHandler;
import com.pragma.powerup.application.mapper.CapacityPageMapper;
import com.pragma.powerup.domain.api.ICapacityListServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CapacityListHandler implements ICapacityListHandler {
    private final ICapacityListServicePort servicePort;
    private final CapacityPageMapper mapper;

    @Override
    public Mono<CapacityPageResponseDto> list(CapacityPageRequestDto request) {
        return servicePort.list(mapper.toQuery(request)).map(mapper::toResponse);
    }
}
