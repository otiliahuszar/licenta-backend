package com.fsega.timetable.model.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class CourseEditDto {

    @NotNull
    private LocalDate date;

    @NotNull
    @Min(0)
    @Max(23)
    private Integer startHour;

    @NotNull
    @Min(1)
    @Max(24)
    private Integer endHour;

    private String title;
    private String description;
    private String location;
    private String resources;
    private Boolean isPublic;

}
