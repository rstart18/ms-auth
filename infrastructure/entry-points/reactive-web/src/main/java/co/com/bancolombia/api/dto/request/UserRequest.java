package co.com.bancolombia.api.dto.request;

import lombok.Data;

@Data
public class UserRequest {
    private String name;
    private String lastname;
    private String email;
    private String identityDocument;
    private String phone;
    private Long roleId;
    private Integer baseSalary;
}
