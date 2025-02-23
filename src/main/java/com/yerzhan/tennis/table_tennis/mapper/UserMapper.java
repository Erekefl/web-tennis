package com.yerzhan.tennis.table_tennis.mapper;

import com.yerzhan.tennis.table_tennis.dto.UserDTO;
import com.yerzhan.tennis.table_tennis.entity.Users;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserMapper {
    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "accountNonExpired", ignore = true)
    @Mapping(target = "accountNonLocked", ignore = true)
    @Mapping(target = "credentialsNonExpired", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    Users toEntity(UserDTO dto);

    @Mapping(target = "password", ignore = true)
    UserDTO toDto(Users entity);
} 