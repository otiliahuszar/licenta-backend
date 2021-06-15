package com.fsega.timetable.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fsega.timetable.exception.NotFoundException;
import com.fsega.timetable.mapper.UserMapper;
import com.fsega.timetable.repository.UserRepository;
import org.springframework.stereotype.Service;

import com.fsega.timetable.mapper.CourseMapper;
import com.fsega.timetable.model.external.CourseDto;
import com.fsega.timetable.model.external.CourseFullDto;
import com.fsega.timetable.model.internal.*;
import com.fsega.timetable.repository.CourseRepository;

import lombok.RequiredArgsConstructor;

import static java.util.Optional.ofNullable;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository repository;
    private final UserRepository userRepository;

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

    public List<CourseFullDto> searchCourses(UUID userId, UUID specializationId, UUID subjectId, UUID teacherId,
                                             LocalDateTime start, LocalDateTime end) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " was not found"));
        UUID specId = specializationId;
        UUID teacId = teacherId;
        Integer year = null;

        switch (user.getRole()) {
            case STUDENT:
                year = user.getSemester()
                        .map(Semester::getStudyYear)
                        .orElse(null);
                specId = user.getSemester()
                        .map(Semester::getSpecialization)
                        .map(Specialization::getId)
                        .orElse(null);
                break;
            case TEACHER:
                teacId = user.getId();
                break;
        }
        return repository.searchCourses(specId, subjectId, teacId, start, end, year).stream()
                .map(CourseMapper::toFullDto)
                .collect(Collectors.toList());
    }
}
