package com.fsega.timetable.model.external;

import java.util.UUID;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class IdNameDto {

    private UUID id;
    private String name;

}
