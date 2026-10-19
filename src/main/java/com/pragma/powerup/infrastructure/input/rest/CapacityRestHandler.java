package com.pragma.powerup.infrastructure.input.rest;

import com.pragma.powerup.application.dto.request.CapacityIdsRequestDto;
import com.pragma.powerup.application.dto.request.CapacityPageRequestDto;
import com.pragma.powerup.application.dto.request.CapacityRequestDto;
import com.pragma.powerup.application.handler.ICapacityDeleteHandler;
import com.pragma.powerup.application.handler.ICapacityHandler;
import com.pragma.powerup.application.handler.ICapacityListHandler;
import com.pragma.powerup.infrastructure.constants.ApiParameters;
import com.pragma.powerup.infrastructure.input.rest.support.QueryParams;
import com.pragma.powerup.infrastructure.input.rest.support.RequestValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CapacityRestHandler {
    private final ICapacityHandler handler;
    private final ICapacityListHandler listHandler;
    private final ICapacityDeleteHandler deleteHandler;
    private final RequestValidator validator;

    public Mono<ServerResponse> createCapacity(ServerRequest request) {
        return validator.body(request, CapacityRequestDto.class)
                .flatMap(handler::createCapacity)
                .flatMap(capacity -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON).bodyValue(capacity));
    }

    public Mono<ServerResponse> list(ServerRequest request) {
        return Mono.fromCallable(() -> {
                    var query = new CapacityPageRequestDto();
                    query.setPage(QueryParams.integer(request, ApiParameters.PAGE, query.getPage()));
                    query.setSize(QueryParams.integer(request, ApiParameters.SIZE, query.getSize()));
                    query.setSortBy(QueryParams.text(request, ApiParameters.SORT_BY, query.getSortBy()));
                    query.setDirection(QueryParams.text(request, ApiParameters.DIRECTION, query.getDirection()));
                    return query;
                })
                .flatMap(validator::validate)
                .flatMap(listHandler::list)
                .flatMap(page -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(page));
    }

    public Mono<ServerResponse> existingIds(ServerRequest request) {
        return ids(request)
                .flatMap(ids -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                        .body(listHandler.existingIds(ids.getIds()), Long.class));
    }

    public Mono<ServerResponse> detailsByIds(ServerRequest request) {
        return ids(request)
                .flatMap(ids -> listHandler.detailsByIds(ids.getIds()))
                .flatMap(details -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(details));
    }

    public Mono<ServerResponse> deleteCapacities(ServerRequest request) {
        return ids(request)
                .flatMap(ids -> deleteHandler.deleteCapacities(ids.getIds()))
                .then(ServerResponse.noContent().build());
    }

    private Mono<CapacityIdsRequestDto> ids(ServerRequest request) {
        return Mono.fromCallable(() -> {
            var query = new CapacityIdsRequestDto();
            query.setIds(QueryParams.longList(request, ApiParameters.IDS));
            return query;
        }).flatMap(validator::validate);
    }
}
