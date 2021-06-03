package com.fsega.timetable.config.ldap;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LdapUser {

    private String firstName;
    private String lastName;
    private String username;
    private String password;

}