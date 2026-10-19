package com.pragma.powerup.infrastructure.out.rest.adapter;

import com.pragma.powerup.infrastructure.constants.ApiParameters;
import com.pragma.powerup.infrastructure.constants.ApiPaths;
import com.pragma.powerup.domain.exception.ConflictException;
import com.pragma.powerup.domain.exception.ExceptionMessages;
import com.pragma.powerup.domain.exception.ExternalServiceException;
import com.pragma.powerup.domain.exception.ValidationException;
import com.pragma.powerup.domain.model.Capacity;
import com.pragma.powerup.domain.spi.ITechnologyAssociationPort;
import com.pragma.powerup.infrastructure.out.rest.dto.TechnologyAssociationRequestDto;
import java.time.Duration;
import java.util.concurrent.TimeoutException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Mono;

@Component
public class TechnologyAssociationRestAdapter implements ITechnologyAssociationPort {
    private final WebClient client;
    private final Duration timeout;

    @Override
    public Mono<Void> deleteForCapacities(List<Long> capacityIds) {
        return ReactiveSecurityContextHolder.getContext()
                .map(context -> (Jwt) context.getAuthentication().getPrincipal())
                .switchIfEmpty(Mono.error(new ExternalServiceException(ExceptionMessages.AUTHENTICATION_REQUIRED.getMessage())))
                .flatMap(jwt -> client.delete().uri(builder -> builder.path(ApiPaths.TECHNOLOGY_CAPACITIES)
                                .queryParam(ApiParameters.CAPACITY_IDS, capacityIds.stream().map(String::valueOf)
                                        .collect(Collectors.joining(","))).build())
                        .headers(headers -> headers.setBearerAuth(jwt.getTokenValue()))
                        .retrieve().toBodilessEntity())
                .timeout(timeout)
                .onErrorMap(error -> error instanceof WebClientException || error instanceof TimeoutException,
                        error -> new ExternalServiceException(ExceptionMessages.TECHNOLOGY_SERVICE_UNAVAILABLE.getMessage()))
                .then();
    }

    public TechnologyAssociationRestAdapter(WebClient.Builder builder,
            @Value("${services.technology.url}") String url,
            @Value("${services.technology.timeout}") Duration timeout) {
        this.client = builder.baseUrl(url).build();
        this.timeout = timeout;
    }

    @Override
    public Mono<Void> associate(Capacity capacity) {
        return ReactiveSecurityContextHolder.getContext()
                .map(context -> (Jwt) context.getAuthentication().getPrincipal())
                .switchIfEmpty(Mono.error(new ExternalServiceException(ExceptionMessages.AUTHENTICATION_REQUIRED.getMessage())))
                .flatMap(jwt -> client.post().uri(ApiPaths.TECHNOLOGY_CAPACITIES)
                        .headers(headers -> headers.setBearerAuth(jwt.getTokenValue()))
                        .bodyValue(new TechnologyAssociationRequestDto(capacity.getId(), capacity.getTechnologyIds()))
                        .retrieve()
                        .onStatus(status -> status == HttpStatus.NOT_FOUND || status == HttpStatus.BAD_REQUEST,
                                response -> Mono.just(new ValidationException(ExceptionMessages.TECHNOLOGIES_NOT_FOUND.getMessage())))
                        .onStatus(status -> status == HttpStatus.CONFLICT,
                                response -> Mono.just(new ConflictException(ExceptionMessages.TECHNOLOGIES_ALREADY_ASSOCIATED.getMessage())))
                        .toBodilessEntity())
                .timeout(timeout)
                .onErrorMap(error -> error instanceof WebClientException || error instanceof TimeoutException,
                        error -> new ExternalServiceException(ExceptionMessages.TECHNOLOGY_SERVICE_UNAVAILABLE.getMessage()))
                .then();
    }
}
