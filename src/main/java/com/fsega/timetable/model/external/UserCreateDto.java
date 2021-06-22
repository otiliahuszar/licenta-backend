package com.fsega.timetable.model.external;

import javax.validation.constraints.NotBlank;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDto {

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    private String email;

    @NotBlank
    private String username;

    private String password;

    private boolean receiveEmailNotificationsBeforeCourses;

    private Integer notificationInterval;

    private boolean receiveEmailNotificationsForUpdates;
}
