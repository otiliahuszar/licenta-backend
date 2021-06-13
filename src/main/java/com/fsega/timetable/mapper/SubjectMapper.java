package com.fsega.timetable.mapper;

import com.fsega.timetable.model.external.IdNameDto;
import com.fsega.timetable.model.external.SubjectDto;
import com.fsega.timetable.model.internal.Institution;
import com.fsega.timetable.model.internal.Specialization;
import com.fsega.timetable.model.internal.Subject;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SubjectMapper {

    public static Subject toEntity(SubjectDto dto, Institution institution) {
        if (dto == null) {
            return null;
        }
        return Subject.builder()
                .internalId(dto.getInternalId())
                .name(dto.getName())
                .institution(institution)
                .build();
    }

    public static IdNameDto toIdNameDto(Subject subject) {
        return IdNameDto.builder()
                .id(subject.getId())
                .name(subject.getInternalId())
                .build();
    }
}
