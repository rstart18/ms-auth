package co.com.bancolombia.api.dto.response;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserResponse {
    Long id;
    String name;
    String lastname;
    String email;
    Integer baseSalary;
}
