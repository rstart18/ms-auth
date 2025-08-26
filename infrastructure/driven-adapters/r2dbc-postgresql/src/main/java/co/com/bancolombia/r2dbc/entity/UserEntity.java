package co.com.bancolombia.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table("users")
public class UserEntity {

    @Id
    private Long id;

    private String name;
    private String lastname;

    private LocalDate birthday;
    private String address;

    private String email;

    @Column("identity_document")
    private String identityDocument;

    private String phone;

    @Column("role_id")
    private Long roleId;

    @Column("base_salary")
    private Integer baseSalary;
}
