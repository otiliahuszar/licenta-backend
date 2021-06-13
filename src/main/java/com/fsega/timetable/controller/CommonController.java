package com.fsega.timetable.controller;

import com.fsega.timetable.model.external.IdNameDto;
import com.fsega.timetable.service.SpecializationService;
import com.fsega.timetable.service.SubjectService;
import com.fsega.timetable.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/common")
public class CommonController {

    private final SpecializationService specializationService;
    private final SubjectService subjectService;
    private final UserService userService;

    @GetMapping("/specializations")
    public List<IdNameDto> getSpecializations() {
        return specializationService.getSpecializations();
    }

    @GetMapping("/subjects")
    public List<IdNameDto> getSubjects() {
        return subjectService.getSubjects();
    }

    @GetMapping("/teachers")
    public List<IdNameDto> getTeachers() {
        return userService.getTeachers();
    }
}
