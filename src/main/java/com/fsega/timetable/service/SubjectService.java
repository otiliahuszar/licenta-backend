package com.fsega.timetable.service;

import org.springframework.stereotype.Service;

import com.fsega.timetable.mapper.SubjectMapper;
import com.fsega.timetable.model.external.SubjectDto;
import com.fsega.timetable.model.internal.Institution;
import com.fsega.timetable.model.internal.Subject;
import com.fsega.timetable.repository.SubjectRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository repository;
    private final InstitutionService institutionService;

    Subject createSubject(SubjectDto dto) {
        Institution institution = institutionService.getInstitution();

        return repository.findByInternalIdAndInstitution(dto.getInternalId(), institution)
                .orElseGet(() -> {
                    Subject s = SubjectMapper.toEntity(dto, institution);
                    return repository.save(s);
                });
    }
}
