package com.fsega.timetable.model.internal;

import javax.persistence.Column;
import javax.persistence.Entity;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Subject extends AbstractEntity {

    @Column(nullable = false)
    private String internalId;

    @Column(nullable = false)
    private String name;

    private String description;

}
