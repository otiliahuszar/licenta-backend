package com.fsega.timetable.model.internal;

import java.util.List;

import javax.persistence.*;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Institution extends AbstractEntity {

    @Column(nullable = false)
    private String name;

    private String description;

    private String website;

    private Integer foundedIn;

    @OneToOne
    private Administrator administrator;

    @OneToMany(mappedBy = "institution", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Specialization> specializations;

}
