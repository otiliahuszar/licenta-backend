package com.fsega.timetable.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fsega.timetable.model.internal.Institution;

public interface InstitutionRepository extends JpaRepository<Institution, UUID> {

    Institution findByName(String name);
}
