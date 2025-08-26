package co.com.bancolombia.model.user;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
//import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {
    private Long id;
    private String name;
    private String lastname;
    private String email;
    private String identityDocument;
    private String phone;
    private Long roleId;
    private Integer baseSalary;
    LocalDate birthday;
    String address;
}

