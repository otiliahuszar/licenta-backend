package com.fsega.timetable.controller;

import com.fsega.timetable.config.security.UserDetails;
import com.fsega.timetable.model.external.IdNameDto;
import com.fsega.timetable.service.InstitutionService;
import com.fsega.timetable.service.SpecializationService;
import com.fsega.timetable.service.SubjectService;
import com.fsega.timetable.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/common")
public class CommonController {

    private final InstitutionService institutionService;
    private final SpecializationService specializationService;
    private final SubjectService subjectService;
    private final UserService userService;

    @GetMapping("/institutions")
    public List<IdNameDto> getInstitutions() {
        return institutionService.getInstitutions();
    }

    @GetMapping("/specializations")
    public List<IdNameDto> getSpecializations(@RequestParam(required = false) UUID institutionId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        return specializationService.getSpecializations(userDetails.getId(), institutionId);
    }

    @GetMapping("/subjects")
    public List<IdNameDto> getSubjects(@RequestParam(required = false) UUID institutionId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        return subjectService.getSubjects(userDetails.getId(), institutionId);
    }

    @GetMapping("/teachers")
    public List<IdNameDto> getTeachers(@RequestParam(required = false) UUID institutionId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        return userService.getTeachers(userDetails.getId(), institutionId);
    }
}
