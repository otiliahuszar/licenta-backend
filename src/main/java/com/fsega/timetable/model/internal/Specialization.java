package com.fsega.timetable.model.internal;

import javax.persistence.*;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Specialization extends AbstractEntity {

    @Column(nullable = false)
    private String internalId;

    @Column(nullable = false)
    private String name;

    private String description;

    @ManyToOne
    private Institution institution;

}
