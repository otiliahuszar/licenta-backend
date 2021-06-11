package com.fsega.timetable.config.ldap;

import com.fsega.timetable.model.enums.Role;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LdapUser {

    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private Role role;
    private String specializationId;
    private Integer studyYear;

}