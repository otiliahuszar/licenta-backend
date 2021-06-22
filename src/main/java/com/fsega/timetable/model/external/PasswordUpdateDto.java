package com.fsega.timetable.model.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class PasswordUpdateDto {

    @NotBlank
    private String oldPassword;

    @NotBlank
    private String newPassword;
}
