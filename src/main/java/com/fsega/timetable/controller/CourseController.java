package com.fsega.timetable.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.fsega.timetable.config.security.UserDetails;
import com.fsega.timetable.model.external.CourseEditDto;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.fsega.timetable.model.external.CourseFullDto;
import com.fsega.timetable.service.CourseService;

import lombok.RequiredArgsConstructor;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    @Secured({"ROLE_ADMIN", "ROLE_TEACHER", "ROLE_STUDENT"})
    public List<CourseFullDto> searchCourses(
            @RequestParam(required = false) UUID specializationId,
            @RequestParam(required = false) UUID subjectId,
            @RequestParam(required = false) UUID teacherId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        return courseService.searchCourses(userDetails.getId(), specializationId, subjectId, teacherId, start, end);
    }

    @DeleteMapping("/{courseId}")
    @Secured("ROLE_ADMIN")
    public boolean deleteCourse(@PathVariable UUID courseId) {
        return courseService.deleteCourse(courseId);
    }

    @PutMapping("/{courseId}")
    @Secured({"ROLE_ADMIN", "ROLE_TEACHER"})
    public boolean updateCourse(@PathVariable UUID courseId,
                                @RequestBody @Valid CourseEditDto dto) {
        return courseService.updateCourse(courseId, dto);
    }
}
