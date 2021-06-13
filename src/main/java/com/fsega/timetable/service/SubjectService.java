package com.fsega.timetable.service;

import com.fsega.timetable.model.external.IdNameDto;
import org.springframework.stereotype.Service;

import com.fsega.timetable.mapper.SubjectMapper;
import com.fsega.timetable.model.external.SubjectDto;
import com.fsega.timetable.model.internal.Institution;
import com.fsega.timetable.model.internal.Subject;
import com.fsega.timetable.repository.SubjectRepository;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository repository;
    private final InstitutionService institutionService;

    public List<IdNameDto> getSubjects() {
        Institution institution = institutionService.getInstitution();

        return repository.findByInstitutionOrderByInternalIdAsc(institution).stream()
                .map(SubjectMapper::toIdNameDto)
                .collect(Collectors.toList());
    }

    Subject createSubject(SubjectDto dto) {
        Institution institution = institutionService.getInstitution();

        return repository.findByInternalIdAndInstitution(dto.getInternalId(), institution)
                .orElseGet(() -> {
                    Subject s = SubjectMapper.toEntity(dto, institution);
                    return repository.save(s);
                });
    }
}
