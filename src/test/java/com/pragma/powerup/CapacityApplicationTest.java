package com.pragma.powerup;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.pragma.powerup.infrastructure.out.r2dbc.repository.ICapacityRepository;
import com.pragma.powerup.infrastructure.out.r2dbc.entity.CapacityEntity;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.LongStream;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        useMainMethod = SpringBootTest.UseMainMethod.ALWAYS)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class CapacityApplicationTest {
    private static final AtomicInteger REMOTE_STATUS = new AtomicInteger(204);
    private static final AtomicInteger REMOTE_CALLS = new AtomicInteger();
    private static final AtomicReference<String> REMOTE_BODY = new AtomicReference<>();
    private static final AtomicReference<String> REMOTE_TOKEN = new AtomicReference<>();
    private static final AtomicReference<String> DELETE_QUERY = new AtomicReference<>();
    private static final AtomicInteger LIST_CALLS = new AtomicInteger();
    private static final AtomicReference<String> LIST_QUERY = new AtomicReference<>();
    private static final AtomicReference<String> LIST_RESPONSE = new AtomicReference<>("[]");
    private static final HttpServer REMOTE = startServer();
    @Autowired private WebTestClient client;
    @Autowired private ICapacityRepository repository;
    @Value("${jwt.secret}") private String secret;

    private static HttpServer startServer() {
        try {
            var server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
            server.createContext("/api/v1/technologies/capacities", exchange -> {
                if ("GET".equals(exchange.getRequestMethod())) {
                    LIST_CALLS.incrementAndGet();
                    LIST_QUERY.set(exchange.getRequestURI().getRawQuery());
                    byte[] response = LIST_RESPONSE.get().getBytes(StandardCharsets.UTF_8);
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(200, response.length);
                    exchange.getResponseBody().write(response);
                } else {
                    REMOTE_CALLS.incrementAndGet();
                    if ("DELETE".equals(exchange.getRequestMethod())) {
                        DELETE_QUERY.set(exchange.getRequestURI().getRawQuery());
                    }
                    REMOTE_BODY.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
                    REMOTE_TOKEN.set(exchange.getRequestHeaders().getFirst("Authorization"));
                    exchange.sendResponseHeaders(REMOTE_STATUS.get(), -1);
                }
                exchange.close();
            });
            server.start();
            return server;
        } catch (IOException exception) {
            throw new IllegalStateException(exception);
        }
    }

    @DynamicPropertySource
    static void remoteProperties(DynamicPropertyRegistry registry) {
        registry.add("services.technology.url", () -> "http://127.0.0.1:" + REMOTE.getAddress().getPort() + "/api/v1");
    }

    @AfterAll static void stopServer() { REMOTE.stop(0); }

    @BeforeEach
    void reset() {
        StepVerifier.create(repository.deleteAll()).verifyComplete();
        REMOTE_STATUS.set(204);
        REMOTE_CALLS.set(0);
        LIST_CALLS.set(0);
        LIST_RESPONSE.set("[]");
    }

    @Test
    void deletesCapacitiesAndRollsBackWhenTechnologyCleanupFails() {
        var first = repository.save(new CapacityEntity(null, "Backend", "Services")).block();
        var second = repository.save(new CapacityEntity(null, "Frontend", "Interfaces")).block();
        assertThat(first).isNotNull();
        assertThat(second).isNotNull();
        String uri = "/capacities?ids=" + first.getId() + "," + second.getId();
        String adminToken = token("ADMIN");

        REMOTE_STATUS.set(503);
        client.delete().uri(uri).headers(headers -> headers.setBearerAuth(adminToken))
                .exchange().expectStatus().isEqualTo(503);
        StepVerifier.create(repository.count()).expectNext(2L).verifyComplete();

        REMOTE_STATUS.set(204);
        client.delete().uri(uri).headers(headers -> headers.setBearerAuth(adminToken))
                .exchange().expectStatus().isNoContent();
        StepVerifier.create(repository.count()).expectNext(0L).verifyComplete();
        assertThat(DELETE_QUERY.get()).contains(String.valueOf(first.getId()), String.valueOf(second.getId()));
        assertThat(REMOTE_TOKEN.get()).isEqualTo("Bearer " + adminToken);
    }

    @Test
    void createsCapacityAtBothLimitsAndForwardsTheToken() {
        String token = token("ADMIN");
        for (int count : new int[] {3, 20}) {
            var ids = LongStream.rangeClosed(1, count).boxed().toList();
            post(token, ids).expectStatus().isCreated().expectBody()
                    .jsonPath("$.id").isNumber().jsonPath("$.name").isEqualTo("Backend")
                    .jsonPath("$.technologyIds.length()").isEqualTo(count);
            assertThat(REMOTE_BODY.get()).contains("capacityId", "technologyIds");
            assertThat(REMOTE_TOKEN.get()).isEqualTo("Bearer " + token);
        }
        StepVerifier.create(repository.count()).expectNext(2L).verifyComplete();
    }

    @Test
    void rollsBackCapacityWhenTechnologyRejectsOrFails() {
        for (int status : new int[] {404, 409, 503}) {
            REMOTE_STATUS.set(status);
            post(token("ADMIN"), List.of(1L, 2L, 3L)).expectStatus().isEqualTo(status == 404 ? 400 : status);
            StepVerifier.create(repository.count()).expectNext(0L).verifyComplete();
        }
    }

    @Test
    void rejectsInvalidRequestsAndUnauthorizedUsersBeforeCallingTechnology() {
        post(token("ADMIN"), List.of(1L, 1L, 2L)).expectStatus().isBadRequest();
        post(token("PERSON"), List.of(1L, 2L, 3L)).expectStatus().isForbidden();
        client.post().uri("/capacities").contentType(MediaType.APPLICATION_JSON).bodyValue(Map.of())
                .exchange().expectStatus().isUnauthorized();
        assertThat(REMOTE_CALLS.get()).isZero();
        StepVerifier.create(repository.count()).expectNext(0L).verifyComplete();
    }

    @Test
    void servesStaticOpenApiThroughSwaggerUi() throws IOException {
        String contract = new ClassPathResource("openapi.yaml").getContentAsString(StandardCharsets.UTF_8);
        client.get().uri("/openapi.yaml").exchange().expectStatus().isOk().expectBody(String.class).isEqualTo(contract);
        client.get().uri("/swagger-ui.html").exchange().expectStatus().is3xxRedirection();
        client.get().uri("/v3/api-docs/swagger-config").exchange().expectStatus().isOk()
                .expectBody().jsonPath("$.url").isEqualTo("/api/v1/openapi.yaml");
    }

    @Test
    void returnsRequestedCapacityDetailsWithTechnologySummaries() {
        var backend = repository.save(new CapacityEntity(null, "Backend", "Services")).block();
        assertThat(backend).isNotNull();
        LIST_RESPONSE.set("[{\"capacityId\":" + backend.getId()
                + ",\"technologies\":[{\"id\":7,\"name\":\"Java\"}]}]");

        client.get().uri("/capacities/details?ids=" + backend.getId() + ",999999")
                .headers(headers -> headers.setBearerAuth(token("ADMIN"))).exchange()
                .expectStatus().isOk().expectBody()
                .jsonPath("$.length()").isEqualTo(1)
                .jsonPath("$[0].id").isEqualTo(backend.getId().intValue())
                .jsonPath("$[0].name").isEqualTo("Backend")
                .jsonPath("$[0].technologies[0].id").isEqualTo(7)
                .jsonPath("$[0].technologies[0].name").isEqualTo("Java")
                .jsonPath("$[0].description").doesNotExist();
        assertThat(LIST_CALLS.get()).isEqualTo(1);
    }

    @Test
    void returnsOnlyExistingCapacityIdsForBootcampRegistration() {
        var first = repository.save(new CapacityEntity(null, "Backend", "Services")).block();
        var second = repository.save(new CapacityEntity(null, "Frontend", "Interfaces")).block();
        assertThat(first).isNotNull();
        assertThat(second).isNotNull();

        client.get().uri("/capacities/ids?ids=" + first.getId() + ",999999," + second.getId())
                .headers(headers -> headers.setBearerAuth(token("ADMIN"))).exchange()
                .expectStatus().isOk().expectBody()
                .jsonPath("$[0]").isNumber().jsonPath("$[1]").isNumber()
                .jsonPath("$.length()").isEqualTo(2);
        client.get().uri("/capacities/ids?ids=" + first.getId())
                .headers(headers -> headers.setBearerAuth(token("PERSON"))).exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void listsOnePageAndLooksUpTechnologiesInOneBatch() {
        var backend = repository.save(new CapacityEntity(null, "Backend", "Services")).block();
        var frontend = repository.save(new CapacityEntity(null, "Frontend", "Interfaces")).block();
        assertThat(backend).isNotNull();
        assertThat(frontend).isNotNull();
        LIST_RESPONSE.set("[{\"capacityId\":" + backend.getId()
                + ",\"technologies\":[{\"id\":7,\"name\":\"Java\"}]}]");

        client.get().uri("/capacities?page=0&size=2&sortBy=name&direction=desc")
                .headers(headers -> headers.setBearerAuth(token("ADMIN"))).exchange()
                .expectStatus().isOk().expectBody()
                .jsonPath("$.totalElements").isEqualTo(2)
                .jsonPath("$.totalPages").isEqualTo(1)
                .jsonPath("$.content[0].name").isEqualTo("Frontend")
                .jsonPath("$.content[1].name").isEqualTo("Backend")
                .jsonPath("$.content[1].technologies[0].id").isEqualTo(7)
                .jsonPath("$.content[1].technologies[0].name").isEqualTo("Java");
        assertThat(LIST_CALLS.get()).isEqualTo(1);
        assertThat(LIST_QUERY.get()).contains("capacityIds");

        client.get().uri("/capacities?size=0").headers(headers -> headers.setBearerAuth(token("ADMIN")))
                .exchange().expectStatus().isBadRequest();
        client.get().uri("/capacities").headers(headers -> headers.setBearerAuth(token("PERSON")))
                .exchange().expectStatus().isForbidden();
    }

    private WebTestClient.ResponseSpec post(String token, List<Long> ids) {
        return client.post().uri("/capacities").headers(h -> h.setBearerAuth(token))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("name", " Backend ", "description", "Development", "technologyIds", ids)).exchange();
    }

    private String token(String role) {
        var encoder = new NimbusJwtEncoder(new ImmutableSecret<>(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256")));
        Instant now = Instant.now();
        var claims = JwtClaimsSet.builder().issuer("http://localhost:8081").subject("1")
                .audience(List.of("on-class-technology")).issuedAt(now).expiresAt(now.plusSeconds(300))
                .claim("role", role).build();
        return encoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims)).getTokenValue();
    }
}
