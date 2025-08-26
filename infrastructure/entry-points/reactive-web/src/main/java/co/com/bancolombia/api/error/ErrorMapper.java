package co.com.bancolombia.api.error;

import co.com.bancolombia.model.commons.BusinessException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebInputException;

import java.util.Map;
import java.util.NoSuchElementException;

public final class ErrorMapper {

    private ErrorMapper() {}

    public static MappedError map(Throwable ex) {
        if (ex instanceof BusinessException be) {
            var status = "CONFLICT".equals(be.getCode()) ? HttpStatus.CONFLICT : HttpStatus.BAD_REQUEST;
            return new MappedError(status, Map.of(
                "field", be.getField(),
                "message", be.getMessage(),
                "code", be.getCode()
            ));
        }

        if (ex instanceof NoSuchElementException) {
            return new MappedError(HttpStatus.NOT_FOUND, Map.of(
                "field", "resource",
                "message", "No encontrado",
                "code", "NOT_FOUND"
            ));
        }

        if (ex instanceof WebExchangeBindException bind) {
            var first = bind.getFieldErrors().stream().findFirst();
            return new MappedError(HttpStatus.BAD_REQUEST, Map.of(
                "field", first.map(f -> f.getField()).orElse("validation"),
                "message", first.map(f -> f.getDefaultMessage()).orElse("Request inválido"),
                "code", "VALIDATION_ERROR"
            ));
        }

        if (ex instanceof ServerWebInputException
            || ex instanceof DecodingException
            || ex.getCause() instanceof MismatchedInputException) {
            return new MappedError(HttpStatus.BAD_REQUEST, Map.of(
                "field", "request",
                "message", "Cuerpo o parámetros inválidos",
                "code", "BAD_REQUEST"
            ));
        }

        if (ex instanceof ResponseStatusException rse) {
            var status = HttpStatus.resolve(rse.getStatusCode().value());
            return new MappedError(status != null ? status : HttpStatus.INTERNAL_SERVER_ERROR, Map.of(
                "field", "request",
                "message", rse.getReason() != null ? rse.getReason() : "Error",
                "code", rse.getStatusCode().toString()
            ));
        }

        return new MappedError(HttpStatus.INTERNAL_SERVER_ERROR, Map.of(
            "field", "internal",
            "message", "Error inesperado",
            "code", "INTERNAL_ERROR"
        ));
    }

    public record MappedError(HttpStatus status, Map<String, Object> body) {}
}
