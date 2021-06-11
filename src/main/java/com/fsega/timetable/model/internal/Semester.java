package com.fsega.timetable.model.internal;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.*;

import org.springframework.stereotype.Service;

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

    @OneToMany(mappedBy = "semester")
    private List<User> students;

}
