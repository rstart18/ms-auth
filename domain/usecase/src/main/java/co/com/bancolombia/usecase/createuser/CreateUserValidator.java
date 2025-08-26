package co.com.bancolombia.usecase.createuser;

import co.com.bancolombia.model.user.User;
import reactor.core.publisher.Mono;

/** Valida y normaliza un User para operación de "crear". */
public interface CreateUserValidator {
    /**
     * Normaliza y valida reglas de negocio. Si todo está OK, devuelve el User normalizado.
     * Si hay error de reglas, emite BusinessException.
     */
    Mono<User> validateForCreate(User input);
}
