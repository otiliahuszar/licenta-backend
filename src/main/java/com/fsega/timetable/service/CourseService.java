package com.fsega.timetable.service;

import org.springframework.stereotype.Service;

import com.fsega.timetable.model.external.CourseDto;
import com.fsega.timetable.model.internal.*;
import com.fsega.timetable.repository.CourseRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository repository;

    public Course createCourse(CourseDto dto, Semester semester, User teacher, Subject subject) {
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
}
