package com.fsega.timetable.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.fsega.timetable.model.internal.Semester;
import com.fsega.timetable.model.internal.Specialization;
import com.fsega.timetable.repository.SemesterRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SemesterService {

    private final SemesterRepository repository;

    public Semester createSemester(LocalDate startDate, LocalDate endDate,
                                   Integer studyYear, Specialization specialization) {
        Semester semester = repository.findOverlappingSemester(startDate, endDate, studyYear, specialization.getId())
                .orElseGet(Semester::new);

        semester.setStartDate(startDate);
        semester.setEndDate(endDate);
        semester.setStudyYear(studyYear);
        semester.setSpecialization(specialization);
        return repository.save(semester);
    }
}
