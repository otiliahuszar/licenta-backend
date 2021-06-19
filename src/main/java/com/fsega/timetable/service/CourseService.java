package com.fsega.timetable.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fsega.timetable.exception.NotFoundException;
import com.fsega.timetable.mapper.UserMapper;
import com.fsega.timetable.model.external.CourseEditDto;
import com.fsega.timetable.model.external.CourseMultipleEditDto;
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
        Course course = CourseMapper.toEntity(dto, semester, teacher, subject);
        return repository.save(course);
    }

    public boolean deleteCourse(UUID courseId) {
        repository.deleteById(courseId);
        return true;
    }

    public boolean updateCourse(UUID courseId, CourseEditDto dto) {
        Course course = repository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course with id " + courseId + " was not found"));

        course.setDate(dto.getDate());
        course.setStartHour(dto.getStartHour());
        course.setEndHour(dto.getEndHour());
        course.setTitle(dto.getTitle());
        course.setDescription(dto.getDescription());
        course.setLocation(dto.getLocation());
        course.setResources(dto.getResources());
        course.setPublic(dto.getIsPublic());

        repository.save(course);
        return true;
    }

    public Integer updateMultipleCourses(UUID teacherId, UUID courseId, CourseMultipleEditDto dto) {
        Course course = repository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course with id " + courseId + " was not found"));

        UUID specializationId = null;
        UUID subjectId = null;
        if (dto.isAllFromSpecialization()) {
            specializationId = course.getSemester().getSpecialization().getId();
        }
        if (dto.isAllFromSubject()) {
            subjectId = course.getSubject().getId();
        }
        Set<Course> courses = repository.searchCoursesForMultipleEdit(
                dto.isAllFromSpecialization(), specializationId,
                dto.isAllFromSubject(), subjectId,
                teacherId, course.getSemester().getEndDate());

        courses.forEach(c -> updateCourse(c, dto));
        repository.saveAll(courses);
        return courses.size();
    }

    private void updateCourse(Course course, CourseMultipleEditDto dto) {
        if (dto.isEditIsPublic()) {
            course.setPublic(dto.getIsPublic() != null && dto.getIsPublic());
        }
        if (dto.isEditTitle()) {
            course.setTitle(dto.getTitle());
        }
        if (dto.isEditDescription()) {
            course.setDescription(dto.getDescription());
        }
        if (dto.isEditLocation()) {
            course.setLocation(dto.getLocation());
        }
        if (dto.isEditResources()) {
            course.setResources(dto.getResources());
        }
    }

    public List<CourseFullDto> searchCourses(UUID userId, UUID specializationId, UUID subjectId, UUID teacherId,
                                             LocalDate start, LocalDate end) {
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
