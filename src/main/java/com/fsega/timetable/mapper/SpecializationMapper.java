package com.fsega.timetable.mapper;

import com.fsega.timetable.model.external.SpecializationDto;
import com.fsega.timetable.model.internal.Institution;
import com.fsega.timetable.model.internal.Specialization;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SpecializationMapper {

    public static Specialization toEntity(SpecializationDto dto, Institution institution) {
        if (dto == null) {
            return null;
        }
        return Specialization.builder()
                .internalId(dto.getInternalId())
                .name(dto.getName())
                .institution(institution)
                .build();
    }
}
