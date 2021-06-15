package com.fsega.timetable.controller;

import com.fsega.timetable.config.security.UserDetails;
import com.fsega.timetable.model.external.IdNameDto;
import com.fsega.timetable.service.SpecializationService;
import com.fsega.timetable.service.SubjectService;
import com.fsega.timetable.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        return specializationService.getSpecializations(userDetails.getId());
    }

    @GetMapping("/subjects")
    public List<IdNameDto> getSubjects() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        return subjectService.getSubjects(userDetails.getId());
    }

    @GetMapping("/teachers")
    public List<IdNameDto> getTeachers() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        return userService.getTeachers(userDetails.getId());
    }
}
