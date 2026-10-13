package com.pragma.powerup.application.handler;

import java.util.List;
import reactor.core.publisher.Mono;

public interface ICapacityDeleteHandler {
    Mono<Void> deleteCapacities(List<Long> ids);
}
