package com.fsega.timetable.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fsega.timetable.model.internal.Institution;
import com.fsega.timetable.model.internal.Specialization;
import org.springframework.data.jpa.repository.Query;

public interface SpecializationRepository extends JpaRepository<Specialization, UUID> {

    Optional<Specialization> findByInternalIdAndInstitution(String internalId, Institution institution);

    List<Specialization> findByInstitutionOrderByInternalIdAsc(Institution institution);

    List<Specialization> findAllByInstitutionAndIdInOrderByInternalIdAsc(Institution institution, Set<UUID> ids);
}
