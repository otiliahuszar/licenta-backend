package com.fsega.timetable.model.internal;

import java.util.List;

import javax.persistence.*;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Promotion extends AbstractEntity {

    @Column(nullable = false)
    private Integer endYear;

    @ManyToOne
    private Specialization specialization;

    @OneToMany
    @JoinTable(
            name = "promotion_students",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "promotion_id")
    )
    private List<Student> students;
}
