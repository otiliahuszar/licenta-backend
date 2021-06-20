package com.fsega.timetable.mapper;

import com.fsega.timetable.model.external.IdNameDto;
import com.fsega.timetable.model.internal.Institution;
import lombok.experimental.UtilityClass;

@UtilityClass
public class InstitutionMapper {

    public static IdNameDto toIdNameDto(Institution institution) {
        if (institution == null) {
            return null;
        }
        return IdNameDto.builder()
                .id(institution.getId())
                .internalId(institution.getName())
                .name(institution.getDescription())
                .build();
    }
}
