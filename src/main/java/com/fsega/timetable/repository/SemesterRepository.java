package com.fsega.timetable.repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fsega.timetable.model.internal.Semester;

public interface SemesterRepository extends JpaRepository<Semester, UUID> {

    @Query("SELECT s FROM Semester s WHERE :startDate < s.endDate AND :endDate > s.startDate " +
            "AND s.studyYear = :studyYear AND s.specialization.id = :specializationId")
    Optional<Semester> findOverlappingSemester(@Param("startDate") LocalDate start,
                                               @Param("endDate") LocalDate end,
                                               @Param("studyYear") Integer studyYear,
                                               @Param("specializationId") UUID specializationId);

    Optional<Semester> findByStudyYearAndSpecialization_InternalId(Integer studyYear, String internalId);
}
