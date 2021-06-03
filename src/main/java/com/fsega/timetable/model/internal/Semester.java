package com.fsega.timetable.model.internal;

import java.time.LocalDate;

import javax.persistence.*;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Semester extends AbstractEntity {

    @Column(nullable = false)
    private String internalId;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private Integer studyYear;

    @ManyToOne
    private Specialization specialization;

}
