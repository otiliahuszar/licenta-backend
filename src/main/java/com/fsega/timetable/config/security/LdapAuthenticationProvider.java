package com.fsega.timetable.config.security;


import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.fsega.timetable.config.ldap.LdapUserRepository;
import com.fsega.timetable.mapper.UserMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LdapAuthenticationProvider implements AuthenticationProvider {

    private final LdapUserRepository ldapUserRepository;
    private final PasswordEncoder encoder;

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) authentication;
        String username = String.valueOf(auth.getPrincipal());
        String password = String.valueOf(auth.getCredentials());

        UserDetails user = ldapUserRepository.findByUsername(username)
                .map(UserMapper::toUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("User with username " + username + " was not found"));

        if (!encoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Bad Credentials");
        }
        user.clearPassword();
        return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}