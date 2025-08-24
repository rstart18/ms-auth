package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.request.UserRequest;
import co.com.bancolombia.api.mapper.dto.UserDtoMapper;
import co.com.bancolombia.model.commons.BusinessException;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.r2dbc.mapper.entity.UserEntityMapper;
import co.com.bancolombia.usecase.createuser.CreateUserUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

@Slf4j
@Component
@RequiredArgsConstructor
public class Handler {
    private final UserDtoMapper userDtoMapper;
    private final CreateUserUseCase createUserUseCase;

    public Mono<ServerResponse> createUser(ServerRequest request) {
        return request.bodyToMono(UserRequest.class)
            .doOnNext(req -> log.debug("[createUser] request: {}", req))
            .map(userDtoMapper::toDomain)
            .doOnNext(dom -> log.debug("[createUser] domain mapped: {}", dom))
            .flatMap(dom -> {
                log.debug("[createUser] calling use case with email={}", dom.getEmail());
                return createUserUseCase.execute(dom);
            })
            .doOnNext(saved -> log.debug("[createUser] saved domain: {}", saved))
            .flatMap(user -> ServerResponse
                .created(location(user))
                .bodyValue(userDtoMapper.toResponse(user)))
            .checkpoint("handler:createUser", true)
            .doOnError(e -> log.error("[createUser] failed", e))
            .onErrorResume(this::mapError);
    }

    private URI location(User u) {
        return URI.create("/api/v1/usuarios/" + u.getId());
    }

    private Mono<ServerResponse> mapError(Throwable t) {
        if (t instanceof BusinessException be) {
            HttpStatus status = switch (be.getCode()) {
                case "CONFLICT" -> HttpStatus.CONFLICT;
                case "INVALID_FIELD" -> HttpStatus.BAD_REQUEST;
                default -> HttpStatus.UNPROCESSABLE_ENTITY;
            };
            return ServerResponse.status(status).bodyValue(be.getMessage());
        }
        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .bodyValue("Error inesperado");
    }
}
