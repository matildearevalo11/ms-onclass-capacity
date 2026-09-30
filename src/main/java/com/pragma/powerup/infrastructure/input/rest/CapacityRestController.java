package com.pragma.powerup.infrastructure.input.rest;

import com.pragma.powerup.application.dto.request.CapacityRequestDto;
import com.pragma.powerup.application.dto.response.CapacityResponseDto;
import com.pragma.powerup.application.handler.ICapacityHandler;
import com.pragma.powerup.application.handler.ICapacityListHandler;
import com.pragma.powerup.application.dto.request.CapacityPageRequestDto;
import com.pragma.powerup.application.dto.response.CapacityPageResponseDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/capacities")
@RequiredArgsConstructor
public class CapacityRestController {
    private final ICapacityHandler handler;
    private final ICapacityListHandler listHandler;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<CapacityPageResponseDto> list(@Valid @ModelAttribute CapacityPageRequestDto request) {
        return listHandler.list(request);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<CapacityResponseDto> createCapacity(@Valid @RequestBody CapacityRequestDto request) {
        return handler.createCapacity(request);
    }
}
