package com.fsega.timetable.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fsega.timetable.model.internal.Institution;
import com.fsega.timetable.model.internal.Subject;

public interface SubjectRepository extends JpaRepository<Subject, UUID> {

    Optional<Subject> findByInternalIdAndInstitution(String internalId, Institution institution);
}
