package co.com.bancolombia.api.config;

import co.com.bancolombia.api.Handler;
import co.com.bancolombia.api.RouterRest;
import co.com.bancolombia.api.error.ErrorMapper;
import co.com.bancolombia.api.mapper.dto.UserDtoMapper;
import co.com.bancolombia.model.user.gateways.UserRepository;
import co.com.bancolombia.usecase.createuser.CreateUserUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {RouterRest.class, Handler.class})
@WebFluxTest
@Import({ CorsConfig.class, SecurityHeadersConfig.class, ErrorMapper.class, ConfigTest.TestRouter.class })
@ActiveProfiles("test")
class ConfigTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private CreateUserUseCase createUserUseCase;

    @MockitoBean
    private UserDtoMapper userDtoMapper;

    @MockitoBean
    private UserPath userPath;

    @BeforeEach
    void setUp() {
        when(userPath.getCreateUser()).thenReturn("/api/v1/usuarios");
    }

    static class TestRouter {
        @Bean
        RouterFunction<ServerResponse> testRoute() {
            return RouterFunctions.route(RequestPredicates.GET("/api/usecase/path"),
                req -> ServerResponse.ok().contentType(MediaType.TEXT_PLAIN).bodyValue("ok"));
        }
    }

    @Test
    void corsConfigurationShouldAllowOrigins() {
        webTestClient.get()
                .uri("/api/usecase/path")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Security-Policy",
                        "default-src 'self'; frame-ancestors 'self'; form-action 'self'")
                .expectHeader().valueEquals("Strict-Transport-Security", "max-age=31536000;")
                .expectHeader().valueEquals("X-Content-Type-Options", "nosniff")
                .expectHeader().valueEquals("Server", "")
                .expectHeader().valueEquals("Cache-Control", "no-store")
                .expectHeader().valueEquals("Pragma", "no-cache")
                .expectHeader().valueEquals("Referrer-Policy", "strict-origin-when-cross-origin");
    }

}
