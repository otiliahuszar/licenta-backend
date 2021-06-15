package com.fsega.timetable.model.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.*;

import com.fsega.timetable.model.enums.Role;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tt_user")
public class User extends AbstractEntity {

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String username;

    private String password;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Role role;

    @ManyToOne
    private Institution institution;

    @ManyToMany
    @JoinTable(
            name = "semester_students",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id"))
    private List<Semester> semesters;

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public List<Semester> getSemesters() {
        if (semesters == null) {
            semesters = new ArrayList<>();
        }
        return semesters;
    }

    public Optional<Semester> getSemester() {
        return getSemesters().stream().findFirst();
    }

    public void addSemester(Semester semester) {
        getSemesters().add(semester);
        semester.getStudents().add(this);
    }

}
