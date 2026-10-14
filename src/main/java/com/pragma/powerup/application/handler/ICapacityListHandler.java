package com.pragma.powerup.application.handler;

import com.pragma.powerup.application.dto.request.CapacityPageRequestDto;
import com.pragma.powerup.application.dto.response.CapacityPageResponseDto;
import com.pragma.powerup.application.dto.response.CapacityDetailResponseDto;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import java.util.List;

public interface ICapacityListHandler {
    Mono<CapacityPageResponseDto> list(CapacityPageRequestDto request);
    Flux<Long> existingIds(List<Long> ids);
    Mono<List<CapacityDetailResponseDto>> detailsByIds(List<Long> ids);
}
