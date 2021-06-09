package com.fsega.timetable.model.internal;

import java.time.Instant;
import java.util.UUID;

import javax.persistence.*;

import org.hibernate.annotations.*;

import lombok.*;

@Getter
@MappedSuperclass
@AllArgsConstructor
@NoArgsConstructor
abstract class AbstractEntity {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Instant created;

    @Column(nullable = false)
    @UpdateTimestamp
    private Instant updated;

}