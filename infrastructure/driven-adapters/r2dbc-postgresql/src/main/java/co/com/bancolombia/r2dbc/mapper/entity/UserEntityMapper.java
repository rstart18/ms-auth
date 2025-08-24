package co.com.bancolombia.r2dbc.mapper.dto;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.r2dbc.config.MapStructSpringConfig;
import co.com.bancolombia.r2dbc.entity.RoleEntity;
import co.com.bancolombia.r2dbc.entity.UserEntity;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

@Mapper(config = MapStructSpringConfig.class)
public interface UserEntityMapper {

    // ------- Entity -> Domain -------
    @Mappings({
        @Mapping(target = "roleId", source = "role.id")
    })
    User toDomain(UserEntity entity);

    // ------- Domain -> Entity -------
    @Mappings({
        @Mapping(target = "role", source = "roleId", qualifiedByName = "toRoleEntity"),
        @Mapping(target = "id", ignore = true)
    })
    UserEntity toEntity(User domain);

    // ------- Updates (PATCH/PUT) opcional -------
    @InheritConfiguration(name = "toEntity")
    void updateEntityFromDomain(User domain, @MappingTarget UserEntity target);

    // ------- Helpers -------
    @Named("toRoleEntity")
    default RoleEntity toRoleEntity(Long roleId) {
        if (roleId == null) return null;
        return RoleEntity.builder().id(roleId).build();
    }
}
