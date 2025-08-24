package co.com.bancolombia.model.commons;

public class BusinessException extends RuntimeException {
    private final String code;

    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }
    public String getCode() { return code; }

    public static BusinessException invalidField(String field, String msg) {
        return new BusinessException("INVALID_FIELD", "Campo [" + field + "]: " + msg);
    }

    public static BusinessException conflict(String field, String msg) {
        return new BusinessException("CONFLICT", "Conflicto [" + field + "]: " + msg);
    }
}
