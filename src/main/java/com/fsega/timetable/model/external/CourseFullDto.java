package com.fsega.timetable.model.external;

import java.time.LocalDate;
import java.util.UUID;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class CourseFullDto {

    private UUID id;
    private LocalDate date;
    private Integer startHour;
    private Integer endHour;
    private Integer studyYear;

    private IdNameDto institution;
    private IdNameDto specialization;
    private IdNameDto subject;
    private IdNameDto teacher;

    private String title;
    private String description;
    private String location;
    private String resources;
    private Boolean isPublic;

    private boolean enrolled;

}
