package com.fsega.timetable.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import com.fsega.timetable.model.external.CourseFullDto;
import com.fsega.timetable.service.CourseService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    @GetMapping("/admin")
    @Secured("ROLE_ADMIN")
    public List<CourseFullDto> searchCourses(
            @RequestParam(required = false) UUID specializationId,
            @RequestParam(required = false) UUID subjectId,
            @RequestParam(required = false) UUID teacherId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return courseService.searchCourses(specializationId, subjectId, teacherId, start, end);
    }
}
