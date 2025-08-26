package co.com.bancolombia.model.commons;

import java.util.Map;

public class BusinessException extends RuntimeException {
    private final String code;
    private final String field;
    private final Map<String, Object> details;

    public BusinessException(String code, String field, String message, Map<String, Object> details) {
        super(message);
        this.code = code;
        this.field = field;
        this.details = details;
    }

    public static BusinessException invalidField(String field, String msg) {
        return new BusinessException("INVALID_FIELD", field, msg, null);
    }

    public static BusinessException conflict(String field, String msg) {
        return new BusinessException("CONFLICT", field, msg, null);
    }

    public static BusinessException internal(String msg) {
        return new BusinessException("INTERNAL_ERROR", "internal", msg, null);
    }

    public String getCode() { return code; }
    public String getField() { return field; }
    public Map<String, Object> getDetails() { return details; }
}
