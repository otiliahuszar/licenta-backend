package com.fsega.timetable.model.internal;

import javax.persistence.*;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Student extends User {

    private String numarMatricol;

}
