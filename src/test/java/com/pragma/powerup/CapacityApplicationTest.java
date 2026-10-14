package com.pragma.powerup;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.pragma.powerup.infrastructure.out.r2dbc.repository.ICapacityRepository;
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
    private static final HttpServer REMOTE = startServer();
    @Autowired private WebTestClient client;
    @Autowired private ICapacityRepository repository;
    @Value("${jwt.secret}") private String secret;

    private static HttpServer startServer() {
        try {
            var server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
            server.createContext("/api/v1/technologies/capacities", exchange -> {
                REMOTE_CALLS.incrementAndGet();
                REMOTE_BODY.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
                REMOTE_TOKEN.set(exchange.getRequestHeaders().getFirst("Authorization"));
                exchange.sendResponseHeaders(REMOTE_STATUS.get(), -1);
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
