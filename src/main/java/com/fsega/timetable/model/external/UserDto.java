package com.fsega.timetable.model.external;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fsega.timetable.model.enums.Role;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {

    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;

}
