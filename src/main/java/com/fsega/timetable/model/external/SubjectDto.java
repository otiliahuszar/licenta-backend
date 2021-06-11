package com.fsega.timetable.model.external;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class SubjectDto {

    private String internalId;
    private String name;

}
