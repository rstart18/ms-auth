package co.com.bancolombia.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class SpringDocConfig {

    /**
     * Sirve el archivo openapi.yaml desde resources/static
     */
    @Bean
    public RouterFunction<ServerResponse> openApiRouter() {
        return route(GET("/v3/api-docs"), request -> {
            try {
                Resource resource = new ClassPathResource("static/openapi.yaml");
                String content = resource.getContentAsString(StandardCharsets.UTF_8);

                return ServerResponse.ok()
                    .contentType(MediaType.parseMediaType("application/x-yaml"))
                    .bodyValue(content);
            } catch (IOException e) {
                return ServerResponse.notFound().build();
            }
        }).andRoute(GET("/v3/api-docs.yaml"), request -> {
            try {
                Resource resource = new ClassPathResource("static/openapi.yaml");
                String content = resource.getContentAsString(StandardCharsets.UTF_8);

                return ServerResponse.ok()
                    .contentType(MediaType.parseMediaType("application/x-yaml"))
                    .bodyValue(content);
            } catch (IOException e) {
                return ServerResponse.notFound().build();
            }
        });
    }
}
