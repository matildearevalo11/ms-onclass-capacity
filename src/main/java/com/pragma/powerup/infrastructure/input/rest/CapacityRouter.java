package com.pragma.powerup.infrastructure.input.rest;

import com.pragma.powerup.infrastructure.constants.ApiPaths;
import com.pragma.powerup.infrastructure.exceptionhandler.ErrorHandlingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class CapacityRouter {
    @Bean
    RouterFunction<ServerResponse> capacityRoutes(CapacityRestHandler handler, ErrorHandlingFilter errors) {
        return RouterFunctions.route()
                .POST(ApiPaths.CAPACITIES, handler::createCapacity)
                .GET(ApiPaths.CAPACITIES, handler::list)
                .GET(ApiPaths.CAPACITY_IDS, handler::existingIds)
                .GET(ApiPaths.CAPACITY_DETAILS, handler::detailsByIds)
                .DELETE(ApiPaths.CAPACITIES, handler::deleteCapacities)
                .filter(errors)
                .build();
    }
}
