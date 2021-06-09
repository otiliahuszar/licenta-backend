package com.fsega.timetable.mapper;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.fsega.timetable.config.ldap.LdapUser;
import com.fsega.timetable.config.security.UserDetails;
import com.fsega.timetable.model.external.UserCreateDto;
import com.fsega.timetable.model.external.UserDto;
import com.fsega.timetable.model.internal.User;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UserMapper {

    public static UserDto toDto(User entity) {
        if (entity == null) {
            return null;
        }
        return UserDto.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .email(entity.getEmail())
                .username(entity.getUsername())
                .role(entity.getRole())
                .build();
    }

    public static User toEntity(LdapUser user) {
        if (user == null) {
            return null;
        }
        return User.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }

    public static User toEntity(UserCreateDto dto) {
        if (dto == null) {
            return null;
        }
        return User.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .username(dto.getUsername())
                .build();
    }

    public static UserDetails toUserDetails(User entity) {
        if (entity == null) {
            return null;
        }
        return UserDetails.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .authorities(List.of(new SimpleGrantedAuthority(entity.getRole().toString())))
                .build();
    }

}
