package com.fsega.timetable.controller;

import java.util.List;
import java.util.UUID;

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
    public List<CourseFullDto> searchCourses(@RequestParam(required = false) UUID specializationId,
                                             @RequestParam(required = false) UUID subjectId,
                                             @RequestParam(required = false) UUID teacherId) {
        return courseService.searchCourses(specializationId, subjectId, teacherId);
    }
}
