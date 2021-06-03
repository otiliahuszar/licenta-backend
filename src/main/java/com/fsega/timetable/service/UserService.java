package com.fsega.timetable.service;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fsega.timetable.exception.BadRequestException;
import com.fsega.timetable.exception.NotFoundException;
import com.fsega.timetable.config.ldap.LdapUserRepository;
import com.fsega.timetable.mapper.UserMapper;
import com.fsega.timetable.model.external.UserCreateDto;
import com.fsega.timetable.model.external.UserDto;
import com.fsega.timetable.model.internal.User;
import com.fsega.timetable.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final LdapUserRepository ldapUserRepository;
    private final PasswordEncoder encoder;

    public UserDto getUser(UUID id) {
        return userRepository.findById(id)
                .map(UserMapper::toDto)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " was not found"));
    }

    public UserDto getLdapUser(String username) {
        return ldapUserRepository.findByUsername(username)
                .map(UserMapper::toDto)
                .orElseThrow(() -> new NotFoundException("User with id " + username + " was not found"));
    }

    public UserDto createUser(UserCreateDto dto) {
        validateUsername(dto.getUsername());
        validateEmail(dto.getEmail());

        User s = UserMapper.toEntity(dto);
        s.setPassword(encoder.encode(dto.getPassword()));

        User user = userRepository.save(s);
        return UserMapper.toDto(user);
    }

    private void validateUsername(String username) {
        userRepository.findByUsername(username)
                .ifPresent(u -> {
                    throw new BadRequestException("User with username " + username + " already exists");
                });
    }

    private void validateEmail(String email) {
        userRepository.findByEmail(email)
                .ifPresent(u -> {
                    throw new BadRequestException("User with email " + email + " already exists");
                });
    }

}
