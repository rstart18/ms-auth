package co.com.bancolombia.api;

import co.com.bancolombia.api.config.UserPath;
import co.com.bancolombia.api.dto.request.UserRequest;
import co.com.bancolombia.api.error.ErrorMapper;
import co.com.bancolombia.api.mapper.dto.UserDtoMapper;
import co.com.bancolombia.model.commons.BusinessException;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.usecase.createuser.CreateUserUseCase;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class Handler {

    private final UserDtoMapper userDtoMapper;
    private final CreateUserUseCase createUserUseCase;
    private final Validator validator;
    private final UserPath userPath;

    public Mono<ServerResponse> createUser(ServerRequest request) {
        return request.bodyToMono(UserRequest.class)
            .doOnNext(req -> log.debug("[createUser] request: {}", req))
            .flatMap(this::validateDto)
            .map(userDtoMapper::toDomain)
            .doOnNext(dom -> log.debug("[createUser] domain mapped: {}", dom))
            .flatMap(createUserUseCase::execute)
            .doOnNext(saved -> log.debug("[createUser] saved domain: {}", saved))
            .flatMap(user -> ServerResponse
                .created(location(request, user.getId()))
                .bodyValue(userDtoMapper.toResponse(user)))
            .checkpoint("handler:createUser", true)
            .doOnError(e -> log.error("[createUser] failed", e))
            .onErrorResume(this::mapError);
    }

    // ---- helpers ----
    private Mono<UserRequest> validateDto(UserRequest dto) {
        Set<ConstraintViolation<UserRequest>> violations = validator.validate(dto);
        if (violations.isEmpty()) return Mono.just(dto);

        ConstraintViolation<UserRequest> v = violations.iterator().next();
        String field = v.getPropertyPath().toString();
        String msg   = v.getMessage();
        return Mono.error(BusinessException.invalidField(field, msg));
    }

    private URI location(ServerRequest req, Long id) {
        String base = userPath.getCreateUser();
        String path = (base.endsWith("/") ? base : base + "/") + id;
        return org.springframework.web.util.UriComponentsBuilder
            .fromUri(req.uri())
            .replacePath(path)
            .replaceQuery(null)
            .build(true)
            .toUri();
    }

    /**
     * Usa el ErrorMapper existente para consistencia
     */
    private Mono<ServerResponse> mapError(Throwable t) {
        var mappedError = ErrorMapper.map(t);
        return ServerResponse
            .status(mappedError.status())
            .bodyValue(mappedError.body());
    }
}
