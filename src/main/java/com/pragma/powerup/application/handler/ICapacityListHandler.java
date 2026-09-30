package com.pragma.powerup.application.handler;

import com.pragma.powerup.application.dto.request.CapacityPageRequestDto;
import com.pragma.powerup.application.dto.response.CapacityPageResponseDto;
import reactor.core.publisher.Mono;

public interface ICapacityListHandler {
    Mono<CapacityPageResponseDto> list(CapacityPageRequestDto request);
}
