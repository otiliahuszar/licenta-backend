package com.fsega.timetable.model.internal;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import javax.persistence.*;

import lombok.*;

@Getter
@Setter
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

    private String title;

    @Column(columnDefinition = "text")
    private String description;

    @Column(length = 1000)
    private String location;

    @Column(length = 1000)
    private String resources;

    private boolean isPublic;

    @ManyToMany(mappedBy = "publicCourses")
    private List<User> students;

    public LocalDateTime getFullStartDate() {
        return LocalDateTime.of(date, LocalTime.of(startHour, 0));
    }

}
