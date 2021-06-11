package com.fsega.timetable.model.external;

import java.time.LocalDate;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class CourseDto {

    private LocalDate date;
    private Integer startHour;
    private Integer endHour;
    private Integer studyYear;

}
