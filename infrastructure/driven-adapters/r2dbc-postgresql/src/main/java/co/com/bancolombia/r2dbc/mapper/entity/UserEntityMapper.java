package co.com.bancolombia.r2dbc.mapper.entity;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.r2dbc.config.MapStructSpringConfig;
import co.com.bancolombia.r2dbc.entity.RoleEntity;
import co.com.bancolombia.r2dbc.entity.UserEntity;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;


@Mapper(config = MapStructSpringConfig.class)
public interface UserEntityMapper {

    // Entity -> Domain
    User toDomain(UserEntity entity);

    // Domain -> Entity
    @Mappings({
        @Mapping(target = "id", ignore = true)
    })
    UserEntity toEntity(User domain);

    @InheritConfiguration(name = "toEntity")
    void updateEntityFromDomain(User domain, @MappingTarget UserEntity target);
}
