package com.fsega.timetable.mapper;

import com.fsega.timetable.model.external.CourseDto;
import com.fsega.timetable.model.external.CourseFullDto;
import com.fsega.timetable.model.external.IdNameDto;
import com.fsega.timetable.model.internal.*;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CourseMapper {

    public static CourseFullDto toFullDto(Course course) {
        if (course == null) {
            return null;
        }
        Specialization spec = course.getSemester().getSpecialization();

        return CourseFullDto.builder()
                .id(course.getId())
                .date(course.getDate())
                .startHour(course.getStartHour())
                .endHour(course.getEndHour())
                .studyYear(course.getSemester().getStudyYear())
                .institution(InstitutionMapper.toIdNameDto(spec.getInstitution()))
                .specialization(SpecializationMapper.toIdNameDto(spec))
                .subject(SubjectMapper.toIdNameDto(course.getSubject()))
                .teacher(UserMapper.toIdNameDto(course.getTeacher()))
                .title(course.getTitle())
                .description(course.getDescription())
                .location(course.getLocation())
                .resources(course.getResources())
                .isPublic(course.isPublic())
                .build();
    }

    public static Course toEntity(CourseDto dto, Semester semester, User teacher, Subject subject) {
        return Course.builder()
                .date(dto.getDate())
                .startHour(dto.getStartHour())
                .endHour(dto.getEndHour())
                .semester(semester)
                .teacher(teacher)
                .subject(subject)
                .build();
    }
}
