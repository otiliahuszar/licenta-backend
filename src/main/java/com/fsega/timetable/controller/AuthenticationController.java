package com.fsega.timetable.controller;

import javax.validation.Valid;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.fsega.timetable.config.security.*;
import com.fsega.timetable.model.external.*;
import com.fsega.timetable.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthenticationController {

    private final DBAuthenticationProvider dbAuthenticationProvider;
    private final LdapAuthenticationProvider ldapAuthenticationProvider;
    private final JwtUtils jwtUtils;
    private final UserService userService;

    @PostMapping("/login")
    public LoginResponseDto authenticateUser(@RequestBody @Valid LoginDto dto) {
        Authentication authentication = dbAuthenticationProvider.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication, "db");
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        UserDto userDto = userService.getUser(userDetails.getId());
        return new LoginResponseDto(jwt, userDto);
    }

    @PostMapping("/login/ldap")
    public LoginResponseDto authenticateLdapUser(@RequestBody @Valid LoginDto dto) {
        Authentication authentication = ldapAuthenticationProvider.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication, "ldap");
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        UserDto userDto = userService.getLdapUser(userDetails.getUsername());
        return new LoginResponseDto(jwt, userDto);
    }

}
