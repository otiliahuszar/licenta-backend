package com.fsega.timetable.model.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class CourseMultipleEditDto {

    private boolean allFromSpecialization;
    private boolean allFromSubject;

    private boolean editIsPublic;
    private Boolean isPublic;

    private boolean editTitle;
    private String title;

    private boolean editDescription;
    private String description;

    private boolean editLocation;
    private String location;

    private boolean editResources;
    private String resources;
}
