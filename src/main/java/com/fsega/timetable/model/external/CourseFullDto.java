package com.fsega.timetable.model.external;

import java.time.LocalDate;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class CourseFullDto {

    private LocalDate date;
    private Integer startHour;
    private Integer endHour;
    private Integer studyYear;

    private IdNameDto specialization;
    private IdNameDto subject;
    private IdNameDto teacher;

}
