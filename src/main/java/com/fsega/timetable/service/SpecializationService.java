package com.fsega.timetable.service;

import org.springframework.stereotype.Service;

import com.fsega.timetable.mapper.SpecializationMapper;
import com.fsega.timetable.model.external.SpecializationDto;
import com.fsega.timetable.model.internal.Institution;
import com.fsega.timetable.model.internal.Specialization;
import com.fsega.timetable.repository.SpecializationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SpecializationService {

    private final SpecializationRepository repository;
    private final InstitutionService institutionService;

    Specialization createSpecialization(SpecializationDto dto) {
        Institution institution = institutionService.getInstitution();

        return repository.findByInternalIdAndInstitution(dto.getInternalId(), institution)
                .orElseGet(() -> {
                    Specialization s = SpecializationMapper.toEntity(dto, institution);
                    return repository.save(s);
                });
    }
}
