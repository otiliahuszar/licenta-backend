package com.fsega.timetable.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fsega.timetable.exception.NotFoundException;
import com.fsega.timetable.mapper.UserMapper;
import com.fsega.timetable.model.enums.Role;
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

import javax.mail.MessagingException;

import static java.util.Optional.ofNullable;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository repository;
    private final UserRepository userRepository;
    private final EmailService emailService;

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
        sendEmailsForUpdatedCourse(course);
        return true;
    }

    private void sendEmailsForUpdatedCourse(Course course) {
        try {
            for (User s : course.getSemester().getStudents()) {
                emailService.sendCourseUpdateEmail(s, course);
            }
            for (User s : course.getStudents()) {
                emailService.sendCourseUpdateEmail(s, course);
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        }
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
        sendEmailsForUpdatedCourses(dto, courses);
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

    private void sendEmailsForUpdatedCourses(CourseMultipleEditDto dto, Set<Course> courses) {
        Set<User> students = courses.stream()
                .map(Course::getSemester)
                .flatMap(s -> s.getStudents().stream())
                .collect(Collectors.toSet());
        Set<User> publicStudents = courses.stream()
                .flatMap(s -> s.getStudents().stream())
                .collect(Collectors.toSet());
        try {
            for (User s : students) {
                emailService.sendCourseUpdateEmail(s, courses, dto);
            }
            for (User s : publicStudents) {
                emailService.sendCourseUpdateEmail(s, courses, dto);
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public List<CourseFullDto> searchCourses(UUID userId, UUID specializationId, UUID subjectId, UUID teacherId,
                                             LocalDate start, LocalDate end, Boolean isPublic) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " was not found"));

        List<Course> courses = getCourses(specializationId, subjectId, teacherId, start, end, isPublic, user);
        List<Course> publicCourses = getCoursesWithEnrollment(user, subjectId, teacherId, isPublic, start, end);

        return Stream.concat(courses.stream(), publicCourses.stream())
                .map(CourseMapper::toFullDto)
                .collect(Collectors.toList());
    }

    private List<Course> getCourses(UUID specializationId, UUID subjectId, UUID teacherId,
                                    LocalDate start, LocalDate end, Boolean isPublic, User user) {
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
            case EXTERNAL_USER:
                return new ArrayList<>();
        }
        return repository.searchCourses(specId, subjectId, teacId, start, end, year, isPublic);
    }

    private List<Course> getCoursesWithEnrollment(User user, UUID subjectId, UUID teacherId, Boolean isPublic,
                                                  LocalDate start, LocalDate end) {
        return user.getPublicCourses().stream()
                .filter(c -> subjectId == null || c.getSubject().getId().equals(subjectId))
                .filter(c -> teacherId == null || c.getTeacher().getId().equals(teacherId))
                .filter(c -> isPublic == null || c.isPublic() == isPublic)
                .filter(c -> start == null || !c.getDate().isBefore(start))
                .filter(c -> end == null || !c.getDate().isAfter(end))
                .collect(Collectors.toList());
    }

    public List<CourseFullDto> searchPublicCourses(UUID userId, UUID institutionId, UUID specializationId,
                                                   UUID subjectId, UUID teacherId, String title, String description) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " was not found"));
        Set<UUID> enrollments = user.getPublicCourses().stream()
                .map(Course::getId)
                .collect(Collectors.toSet());
        Set<UUID> specializationEnrollments = user.getSemester()
                .map(repository::findBySemester)
                .map(l -> l.stream().map(Course::getId).collect(Collectors.toSet()))
                .orElseGet(HashSet::new);

        String searchTitle = ofNullable(title)
                .map(t -> "%" + t + "%")
                .orElse(null);
        String searchDescription = ofNullable(description)
                .map(t -> "%" + t + "%")
                .orElse(null);
        return repository.searchPublicCourses(institutionId, specializationId, subjectId, teacherId, searchTitle, searchDescription).stream()
                .map(CourseMapper::toFullDto)
                .peek(dto -> dto.setEnrolled(
                        enrollments.contains(dto.getId()) || specializationEnrollments.contains(dto.getId())))
                .collect(Collectors.toList());
    }

    public boolean enrollToCourse(UUID userId, UUID courseId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " was not found"));
        Course course = repository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course with id " + courseId + " was not found"));

        user.addPublicCourse(course);
        userRepository.save(user);
        return true;
    }
}
