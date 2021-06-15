package com.fsega.timetable.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import com.fsega.timetable.model.internal.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;

import com.fsega.timetable.model.internal.Institution;
import com.fsega.timetable.model.internal.Subject;

public interface SubjectRepository extends JpaRepository<Subject, UUID> {

    Optional<Subject> findByInternalIdAndInstitution(String internalId, Institution institution);

    List<Subject> findByInstitutionOrderByInternalIdAsc(Institution institution);

    List<Subject> findAllByInstitutionAndIdInOrderByInternalIdAsc(Institution institution, Set<UUID> ids);

}
