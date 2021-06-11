package com.fsega.timetable.controller;

import java.io.IOException;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import com.fsega.timetable.exception.BadRequestException;
import com.fsega.timetable.service.TimetableService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/timetable")
public class TimetableController {

    private final TimetableService timetableService;

    @PostMapping
    @Secured("ROLE_ADMIN")
    public boolean createTimetable(@RequestHeader(name = "Authorization") String token,
                                   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate semesterStart,
                                   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate semesterEnd,
                                   @RequestBody byte[] file) {

        try {
            timetableService.createTimetable(semesterStart, semesterEnd, file);
            return true;
        } catch (IOException e) {
            throw new BadRequestException("Error while processing CSV file: " + e.getMessage());
        }

    }
}
