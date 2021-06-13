package com.fsega.timetable.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fsega.timetable.model.internal.Course;

public interface CourseRepository extends JpaRepository<Course, UUID> {

    @Query("SELECT c FROM Course c WHERE " +
            "(CAST(:specializationId AS text) IS NULL OR c.semester.specialization.id = :specializationId) AND " +
            "(CAST(:subjectId AS text) IS NULL OR c.subject.id = :subjectId) AND " +
            "(CAST(:teacherId AS text) IS NULL OR c.teacher.id = :teacherId) AND " +
            "(CAST(:startDate AS text) IS NULL OR c.date >= :startDate) AND " +
            "(CAST(:endDate AS text) IS NULL OR c.date <= :endDate) " +
            "ORDER BY c.date ASC, c.startHour ASC")
    List<Course> searchCourses(@Param("specializationId") UUID specializationId,
                               @Param("subjectId") UUID subjectId,
                               @Param("teacherId") UUID teacherId,
                               @Param("startDate") LocalDateTime start,
                               @Param("endDate") LocalDateTime end);
}
