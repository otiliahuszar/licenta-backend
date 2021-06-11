package com.fsega.timetable.model.external;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class SpecializationDto {

    private String internalId;
    private String name;

}
