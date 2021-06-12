package com.fsega.timetable.mapper;

import com.fsega.timetable.model.external.CourseFullDto;
import com.fsega.timetable.model.external.IdNameDto;
import com.fsega.timetable.model.internal.Course;
import com.fsega.timetable.model.internal.Specialization;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CourseMapper {

    public static CourseFullDto toFullDto(Course course) {
        if (course == null) {
            return null;
        }
        Specialization spec = course.getSemester().getSpecialization();

        return CourseFullDto.builder()
                .date(course.getDate())
                .startHour(course.getStartHour())
                .endHour(course.getEndHour())
                .studyYear(course.getSemester().getStudyYear())
                .specialization(IdNameDto.builder()
                        .id(spec.getId())
                        .name(spec.getInternalId())
                        .build())
                .subject(IdNameDto.builder()
                        .id(course.getSubject().getId())
                        .name(course.getSubject().getInternalId())
                        .build())
                .teacher(IdNameDto.builder()
                        .id(course.getTeacher().getId())
                        .name(course.getTeacher().getFullName())
                        .build())
                .build();
    }
}
