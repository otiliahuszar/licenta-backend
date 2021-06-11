package com.fsega.timetable.model.internal;

import javax.persistence.*;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Subject extends AbstractEntity {

    @Column(nullable = false)
    private String internalId;

    private String name;

    @ManyToOne
    private Institution institution;

}
