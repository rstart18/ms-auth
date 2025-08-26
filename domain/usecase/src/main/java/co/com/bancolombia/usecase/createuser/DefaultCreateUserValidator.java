package co.com.bancolombia.usecase.createuser;

import co.com.bancolombia.model.commons.BusinessException;
import co.com.bancolombia.model.user.User;
import reactor.core.publisher.Mono;

import java.util.regex.Pattern;

public class DefaultCreateUserValidator implements CreateUserValidator {

    private static final int MIN_SALARY = 0;
    private static final int MAX_SALARY = 1_500_000;
    private static final Pattern EMAIL_RX =
        Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    @Override
    public Mono<User> validateForCreate(User input) {
        return Mono.justOrEmpty(input)
            .switchIfEmpty(Mono.error(
                BusinessException.invalidField("user", "no puede ser null")))
            .map(this::normalize)       // <- no destruye (no convierte a null)
            .flatMap(this::validateRules);
    }

    private User normalize(User u) {
        return u.toBuilder()
            .name(trim(u.getName()))
            .lastname(trim(u.getLastname()))
            .email(lower(trim(u.getEmail())))
            .identityDocument(trim(u.getIdentityDocument()))
            .phone(onlyDigits(trim(u.getPhone())))
            .address(trim(u.getAddress()))
            .build();
    }

    private Mono<User> validateRules(User u) {
        if (isBlank(u.getName()))
            return Mono.error(BusinessException.invalidField("name", "obligatorio"));
        if (isBlank(u.getLastname()))
            return Mono.error(BusinessException.invalidField("lastname", "obligatorio"));
        if (isBlank(u.getEmail()))
            return Mono.error(BusinessException.invalidField("email", "obligatorio"));
        if (!EMAIL_RX.matcher(u.getEmail()).matches())
            return Mono.error(BusinessException.invalidField("email", "formato inválido"));

        if (isBlank(u.getIdentityDocument()))
            return Mono.error(BusinessException.invalidField("identityDocument", "obligatorio"));
        if (isBlank(u.getPhone()))
            return Mono.error(BusinessException.invalidField("phone", "obligatorio"));

        if (u.getRoleId() == null)
            return Mono.error(BusinessException.invalidField("roleId", "obligatorio"));

        if (u.getBaseSalary() == null)
            return Mono.error(BusinessException.invalidField("baseSalary", "obligatorio"));
        if (u.getBaseSalary() < MIN_SALARY || u.getBaseSalary() > MAX_SALARY)
            return Mono.error(BusinessException.invalidField(
                "baseSalary", "fuera de rango [" + MIN_SALARY + ".." + MAX_SALARY + "]"));

        return Mono.just(u);
    }

    // ---------- Helpers ----------
    private static String trim(String s) { return s == null ? null : s.trim(); }
    private static String lower(String s) { return s == null ? null : s.toLowerCase(); }
    private static String onlyDigits(String s) { return s == null ? null : s.replaceAll("\\D+", ""); }
    private static boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
}
