package com.fsega.timetable.config.security;

import java.io.IOException;
import java.security.InvalidParameterException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fsega.timetable.config.ldap.LdapUserRepository;
import com.fsega.timetable.mapper.UserMapper;
import com.fsega.timetable.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthenticationTokenFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final LdapUserRepository ldapUserRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = request.getHeader("Authorization");

            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.getUsernameFromJwtToken(jwt);

                String issuer = jwtUtils.getIssuer(jwt);
                UserDetails userDetails;

                if ("db".equals(issuer)) {
                    userDetails = userRepository.findByUsername(username)
                            .map(UserMapper::toUserDetails)
                            .orElseThrow(() -> new UsernameNotFoundException("User with username " + username + " was not found"));
                } else if ("ldap".equals(issuer)) {
                    userDetails = ldapUserRepository.findByUsername(username)
                            .map(UserMapper::toUserDetails)
                            .orElseThrow(() -> new UsernameNotFoundException("User with username " + username + " was not found"));
                } else {
                    throw new InvalidParameterException("Unknown token issuer: " + issuer);
                }

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        filterChain.doFilter(request, response);
    }

}
