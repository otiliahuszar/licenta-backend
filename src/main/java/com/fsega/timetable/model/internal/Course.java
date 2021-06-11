package com.fsega.timetable.model.internal;

import java.time.LocalDate;

import javax.persistence.*;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Course extends AbstractEntity {

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private Integer startHour;

    @Column(nullable = false)
    private Integer endHour;

    @ManyToOne
    private Semester semester;

    @ManyToOne
    private Subject subject;

    @ManyToOne
    private User teacher;
}
