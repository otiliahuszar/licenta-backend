package com.fsega.timetable.mapper;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.fsega.timetable.config.security.UserDetails;
import com.fsega.timetable.config.ldap.LdapUser;
import com.fsega.timetable.model.enums.Role;
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

    public static UserDto toDto(LdapUser entity) {
        if (entity == null) {
            return null;
        }
        return UserDto.builder()
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .username(entity.getUsername())
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
                .role(Role.EXTERNAL_USER)
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

    public static UserDetails toUserDetails(LdapUser ldapUser) {
        if (ldapUser == null) {
            return null;
        }
        return UserDetails.builder()
                //.id(ldapUser.getId())
                .username(ldapUser.getUsername())
                //.email(ldapUser.getEmail())
                .password(ldapUser.getPassword())
                //.authorities(List.of(new SimpleGrantedAuthority(ldapUser.getRole().toString())))
                .build();
    }
}
