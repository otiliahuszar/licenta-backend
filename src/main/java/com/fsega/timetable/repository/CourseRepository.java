package com.fsega.timetable.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.fsega.timetable.model.internal.Semester;
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
            "(CAST(:endDate AS text) IS NULL OR c.date <= :endDate) AND " +
            "(CAST(:isPublic AS text) IS NULL OR c.isPublic = :isPublic) AND " +
            "(CAST(:studyYear AS text) IS NULL OR c.semester.studyYear = :studyYear) " +
            "ORDER BY c.date ASC, c.startHour ASC")
    List<Course> searchCourses(@Param("specializationId") UUID specializationId,
                               @Param("subjectId") UUID subjectId,
                               @Param("teacherId") UUID teacherId,
                               @Param("startDate") LocalDate start,
                               @Param("endDate") LocalDate end,
                               @Param("studyYear") Integer studyYear,
                               @Param("isPublic") Boolean isPublic);

    @Query("SELECT c FROM Course c WHERE c.isPublic = TRUE AND " +
            "(CAST(:institutionId AS text) IS NULL OR c.semester.specialization.institution.id = :institutionId) AND " +
            "(CAST(:specializationId AS text) IS NULL OR c.semester.specialization.id = :specializationId) AND " +
            "(CAST(:subjectId AS text) IS NULL OR c.subject.id = :subjectId) AND " +
            "(CAST(:teacherId AS text) IS NULL OR c.teacher.id = :teacherId) AND " +
            "(CAST(:title AS text) IS NULL OR c.title LIKE :title) AND c.date >= NOW() AND " +
            "(CAST(:description AS text) IS NULL OR c.description LIKE :description) " +
            "ORDER BY c.date ASC, c.startHour ASC")
    List<Course> searchPublicCourses(@Param("institutionId") UUID institutionId,
                                     @Param("specializationId") UUID specializationId,
                                     @Param("subjectId") UUID subjectId,
                                     @Param("teacherId") UUID teacherId,
                                     @Param("title") String title,
                                     @Param("description") String description);

    @Query("SELECT c.semester.specialization.id FROM Course c WHERE c.teacher.id = :teacherId")
    Set<UUID> findSpecializationIdsForTeacher(@Param("teacherId") UUID teacherId);

    @Query("SELECT c.subject.id FROM Course c WHERE c.teacher.id = :teacherId")
    Set<UUID> findAllSubjectIdsForTeacher(@Param("teacherId") UUID teacherId);

    @Query("SELECT c.subject.id FROM Course c WHERE c.semester.specialization.id = :specializationId AND " +
            "c.semester.studyYear = :studyYear")
    Set<UUID> findAllSubjectIdsForSpecialization(@Param("specializationId") UUID specializationId,
                                                 @Param("studyYear") Integer studyYear);

    @Query("SELECT c.teacher.id FROM Course c WHERE c.semester.specialization.id = :specializationId AND " +
            "c.semester.studyYear = :studyYear")
    Set<UUID> findAllTeacherIdsForSpecialization(@Param("specializationId") UUID specializationId,
                                                 @Param("studyYear") Integer studyYear);

    @Query("SELECT c FROM Course c WHERE " +
            "((:searchForSpecialization = TRUE AND c.semester.specialization.id = :specializationId) OR " +
            "(:searchForSubject = TRUE AND c.subject.id = :subjectId)) AND " +
            "c.teacher.id = :teacherId AND c.date >= NOW() AND c.date <= :endDate")
    Set<Course> searchCoursesForMultipleEdit(@Param("searchForSpecialization") boolean searchForSpecialization,
                                             @Param("specializationId") UUID specializationId,
                                             @Param("searchForSubject") boolean searchForSubject,
                                             @Param("subjectId") UUID subjectId,
                                             @Param("teacherId") UUID teacherId,
                                             @Param("endDate") LocalDate end);

    List<Course> findBySemester(Semester semester);

    @Query("SELECT c.semester.specialization.id FROM Course c WHERE c.isPublic = TRUE")
    Set<UUID> findAllSpecializationIdsForPublicCourses();

    @Query("SELECT c.subject.id FROM Course c WHERE c.isPublic = TRUE")
    Set<UUID> findAllSubjectIdsForPublicCourses();

    @Query("SELECT c.teacher.id FROM Course c WHERE c.isPublic = TRUE")
    Set<UUID> findAllTeacherIdsForPublicCourses();

    Set<Course> findAllBySemester_IdIn(Set<UUID> semesterIds);
}
