package com.fsega.timetable.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fsega.timetable.mapper.CourseMapper;
import com.fsega.timetable.model.external.CourseDto;
import com.fsega.timetable.model.external.CourseFullDto;
import com.fsega.timetable.model.internal.*;
import com.fsega.timetable.repository.CourseRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository repository;

    Course createCourse(CourseDto dto, Semester semester, User teacher, Subject subject) {
        Course course = Course.builder()
                .date(dto.getDate())
                .startHour(dto.getStartHour())
                .endHour(dto.getEndHour())
                .semester(semester)
                .teacher(teacher)
                .subject(subject)
                .build();
        return repository.save(course);
    }

    public List<CourseFullDto> searchCourses(UUID specializationId, UUID subjectId, UUID teacherId) {
        return repository.searchCourses(specializationId, subjectId, teacherId).stream()
                .map(CourseMapper::toFullDto)
                .collect(Collectors.toList());
    }
}
