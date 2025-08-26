package co.com.bancolombia.api.dto.response;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class UserResponse {
    Long id;
    String name;
    String lastname;
    String email;
    String identityDocument;
    String phone;
    Long roleId;
    Integer baseSalary;
    LocalDate birthday;
    String address;
}
