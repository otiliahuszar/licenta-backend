package com.fsega.timetable.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fsega.timetable.model.internal.Course;

public interface CourseRepository extends JpaRepository<Course, UUID> {
}
