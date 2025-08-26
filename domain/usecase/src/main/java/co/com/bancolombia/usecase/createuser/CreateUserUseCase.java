package co.com.bancolombia.usecase.createuser;

import co.com.bancolombia.model.commons.BusinessException;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class CreateUserUseCase {

    private final UserRepository userRepository;
    private final CreateUserValidator validator;

    public Mono<User> execute(User input) {
        return validator.validateForCreate(input)
            .flatMap(user -> userRepository.existsByEmail(user.getEmail())
                .flatMap(exists -> exists
                    ? Mono.error(BusinessException.conflict("email", "ya se encuentra registrado"))
                    : userRepository.save(user)))
            .onErrorMap(ex -> (ex instanceof BusinessException) ? ex
                : BusinessException.internal("Fallo al crear usuario"));
    }
}
