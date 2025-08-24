package co.com.bancolombia.api.mapper.dto;

import co.com.bancolombia.api.dto.request.UserRequest;
import co.com.bancolombia.api.dto.response.UserResponse;
import co.com.bancolombia.model.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserDtoMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "identityDocument", ignore = true)
    @Mapping(target = "phone", ignore = true)
    @Mapping(target = "roleId", ignore = true) // si no viene en el request
    User toDomain(UserRequest request);

    UserResponse toResponse(User domain);
}
