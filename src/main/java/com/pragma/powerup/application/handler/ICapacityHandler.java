package com.pragma.powerup.application.handler;

import com.pragma.powerup.application.dto.request.CapacityRequestDto;
import com.pragma.powerup.application.dto.response.CapacityResponseDto;
import reactor.core.publisher.Mono;

public interface ICapacityHandler {
    Mono<CapacityResponseDto> createCapacity(CapacityRequestDto request);
}
