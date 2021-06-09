package com.fsega.timetable.config.security;


import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.fsega.timetable.config.ldap.LdapUser;
import com.fsega.timetable.config.ldap.LdapUserRepository;
import com.fsega.timetable.mapper.UserMapper;
import com.fsega.timetable.model.internal.User;
import com.fsega.timetable.service.UserService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LdapAuthenticationProvider implements AuthenticationProvider {

    private final LdapUserRepository ldapUserRepository;
    private final UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) authentication;
        String username = String.valueOf(auth.getPrincipal());
        String password = String.valueOf(auth.getCredentials());

        LdapUser ldapUser = ldapUserRepository.findByUsernameAndPassword(username, password)
                .orElseThrow(() -> new UsernameNotFoundException("User with username " + username + " was not found"));

        User user = userService.createLdapUser(ldapUser, password);
        UserDetails userDetails = UserMapper.toUserDetails(user);
        userDetails.clearPassword();
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}