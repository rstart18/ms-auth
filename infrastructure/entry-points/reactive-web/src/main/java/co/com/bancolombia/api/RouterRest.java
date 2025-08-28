package co.com.bancolombia.api;

import co.com.bancolombia.api.config.UserPath;
import co.com.bancolombia.api.error.ErrorMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.HandlerFilterFunction;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Slf4j
@Configuration
public class RouterRest {
    @Bean
    public RouterFunction<ServerResponse> routerFunction(Handler handler, UserPath path) {
        return route(POST(path.getCreateUser()), handler::createUser)
            .filter(errorToJson());
    }

    private HandlerFilterFunction<ServerResponse, ServerResponse> errorToJson() {
        return (request, next) -> next.handle(request)
            .onErrorResume(ex -> {
                log.error("Unhandled error on {} {}: {}", request.method(), request.path(), ex.toString());
                ErrorMapper.MappedError mapped = ErrorMapper.map(ex);
                return ServerResponse.status(mapped.status())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(mapped.body());
            });
    }
}
