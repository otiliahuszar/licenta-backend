package com.fsega.timetable.model.internal;

import javax.persistence.Column;
import javax.persistence.Entity;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Institution extends AbstractEntity {

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    private String website;

    private Integer foundedIn;

}
