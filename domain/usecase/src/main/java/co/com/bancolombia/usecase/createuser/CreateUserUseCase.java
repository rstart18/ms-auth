package co.com.bancolombia.usecase.createuser;

import co.com.bancolombia.model.commons.BusinessException;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class CreateUserUseCase {
    private static final int MIN_SALARY = 0;
    private static final int MAX_SALARY = 1_500_000; // según criterio
    private static final Pattern EMAIL_RX =
        Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final UserRepository userRepository;

    /**
     * Registra un usuario validando:
     * - name, lastname, email y baseSalary no nulos/ni vacíos
     * - email con formato válido (case-insensitive)
     * - baseSalary en rango [0..1_500_000]
     * - unicidad de email
     */
    public Mono<User> execute(User input) {
        return Mono.justOrEmpty(input)
            .switchIfEmpty(Mono.error(
                BusinessException.invalidField("user", "no puede ser null")))
            .map(this::normalize)
            .flatMap(this::validate)
            .flatMap(user -> userRepository.existsByEmail(user.getEmail())
                .flatMap(exists -> exists
                    ? Mono.error(BusinessException.conflict(
                    "email", "ya se encuentra registrado"))
                    : userRepository.save(user)));
    }

    // ---------- Helpers ----------

    private User normalize(User u) {
        String name = trimToNull(u.getName());
        String lastname = trimToNull(u.getLastname());
        String email = trimToNull(u.getEmail());
        if (email != null) email = email.toLowerCase();

        return u.toBuilder()
            .name(name)
            .lastname(lastname)
            .email(email)
            .build();
    }

    private Mono<User> validate(User u) {
        if (isBlank(u.getName())) {
            return Mono.error(BusinessException.invalidField("name", "obligatorio"));
        }
        if (isBlank(u.getLastname())) {
            return Mono.error(BusinessException.invalidField("lastname", "obligatorio"));
        }
        if (isBlank(u.getEmail())) {
            return Mono.error(BusinessException.invalidField("email", "obligatorio"));
        }
        if (!EMAIL_RX.matcher(u.getEmail()).matches()) {
            return Mono.error(BusinessException.invalidField("email", "formato inválido"));
        }
        if (Objects.isNull(u.getBaseSalary())) {
            return Mono.error(BusinessException.invalidField("baseSalary", "obligatorio"));
        }
        if (u.getBaseSalary() < MIN_SALARY || u.getBaseSalary() > MAX_SALARY) {
            return Mono.error(BusinessException.invalidField(
                "baseSalary", "fuera de rango [" + MIN_SALARY + ".." + MAX_SALARY + "]"));
        }
        return Mono.just(u);
    }

    private static boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }

    private static String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
