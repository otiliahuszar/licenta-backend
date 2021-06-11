package com.fsega.timetable.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fsega.timetable.model.internal.Institution;
import com.fsega.timetable.model.internal.Specialization;

public interface SpecializationRepository extends JpaRepository<Specialization, UUID> {

    Optional<Specialization> findByInternalIdAndInstitution(String internalId, Institution institution);
}
