package com.pragma.powerup.infrastructure.out.rest.adapter;

import com.pragma.powerup.infrastructure.constants.ApiParameters;
import com.pragma.powerup.infrastructure.constants.ApiPaths;
import com.pragma.powerup.domain.exception.ExceptionMessages;
import com.pragma.powerup.domain.exception.ExternalServiceException;
import com.pragma.powerup.domain.model.TechnologySummary;
import com.pragma.powerup.domain.spi.ITechnologyQueryPort;
import com.pragma.powerup.infrastructure.out.rest.dto.CapacityTechnologyListResponseDto;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@Component
public class TechnologyQueryRestAdapter implements ITechnologyQueryPort {
    private static final int IDS_PER_REQUEST = 100;
    private final WebClient client;
    private final Duration timeout;

    public TechnologyQueryRestAdapter(WebClient.Builder builder,
            @Value("${services.technology.url}") String url,
            @Value("${services.technology.timeout}") Duration timeout) {
        this.client = builder.baseUrl(url).build();
        this.timeout = timeout;
    }

    @Override
    public Mono<Map<Long, List<TechnologySummary>>> findByCapacityIds(List<Long> capacityIds) {
        if (capacityIds.isEmpty()) {
            return Mono.just(Map.of());
        }
        return ReactiveSecurityContextHolder.getContext()
                .map(context -> (Jwt) context.getAuthentication().getPrincipal())
                .flatMap(jwt -> Flux.fromIterable(capacityIds).buffer(IDS_PER_REQUEST)
                        .concatMap(ids -> client.get().uri(builder -> builder.path(ApiPaths.TECHNOLOGY_CAPACITIES)
                                        .queryParam(ApiParameters.CAPACITY_IDS, ids.stream().map(String::valueOf)
                                                .collect(Collectors.joining(","))).build())
                                .headers(headers -> headers.setBearerAuth(jwt.getTokenValue()))
                                .retrieve().bodyToFlux(CapacityTechnologyListResponseDto.class)
                                .timeout(timeout))
                        .collectList())
                .onErrorMap(error -> error instanceof WebClientException || error instanceof TimeoutException,
                        error -> new ExternalServiceException(ExceptionMessages.TECHNOLOGY_SERVICE_UNAVAILABLE.getMessage()))
                .map(details -> details.stream().collect(Collectors.toMap(
                        CapacityTechnologyListResponseDto::getCapacityId,
                        item -> item.getTechnologies().stream()
                                .map(technology -> new TechnologySummary(technology.getId(), technology.getName()))
                                .toList())));
    }
}
