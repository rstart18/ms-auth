package co.com.bancolombia.api.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @NotBlank(message = "El apellido es obligatorio")
    private String lastname;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo debe tener un formato válido")
    private String email;

    @NotBlank(message = "El documento de identidad es obligatorio")
    private String identityDocument;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "\\d{7,15}", message = "El teléfono debe contener solo dígitos y tener entre 7 y 15 caracteres")
    private String phone;

    @NotNull(message = "El rol es obligatorio")
    private Long roleId;

    @NotNull(message = "El salario base es obligatorio")
    @Min(value = 0, message = "El salario base no puede ser menor a 0")
    @Max(value = 1_500_000, message = "El salario base no puede ser mayor a 1,500,000")
    private Integer baseSalary;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe estar en el pasado")
    private LocalDate birthday;

    @NotBlank(message = "La dirección es obligatoria")
    @Size(max = 255, message = "La dirección no puede superar los 255 caracteres")
    private String address;
}
