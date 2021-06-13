package com.fsega.timetable.model.internal;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Semester extends AbstractEntity {

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private Integer studyYear;

    @ManyToOne
    private Specialization specialization;

    @ManyToMany(mappedBy = "semesters")
    private List<User> students;

    public List<User> getStudents() {
        if (students == null) {
            students = new ArrayList<>();
        }
        return students;
    }
}
