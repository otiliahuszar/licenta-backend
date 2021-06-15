package com.fsega.timetable.service;

import com.fsega.timetable.exception.NotFoundException;
import com.fsega.timetable.model.external.IdNameDto;
import com.fsega.timetable.model.internal.*;
import com.fsega.timetable.repository.CourseRepository;
import com.fsega.timetable.repository.UserRepository;
import org.springframework.stereotype.Service;

import com.fsega.timetable.mapper.SubjectMapper;
import com.fsega.timetable.model.external.SubjectDto;
import com.fsega.timetable.repository.SubjectRepository;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository repository;
    private final InstitutionService institutionService;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public List<IdNameDto> getSubjects(UUID userId) {
        Institution institution = institutionService.getInstitution();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " was not found"));
        List<Subject> subj = new ArrayList<>();

        switch (user.getRole()) {
            case ADMIN:
                subj = repository.findByInstitutionOrderByInternalIdAsc(institution);
                break;
            case TEACHER:
                Set<UUID> subjIds = courseRepository.findAllSubjectIdsForTeacher(user.getId());
                subj = repository.findAllByInstitutionAndIdInOrderByInternalIdAsc(institution, subjIds);
                break;
            case STUDENT:
                Semester sem = user.getSemester().orElse(null);
                if (sem == null) {
                    break;
                }
                Set<UUID> subjectIds = courseRepository.findAllSubjectIdsForSpecialization(
                        sem.getSpecialization().getId(), sem.getStudyYear());
                subj = repository.findAllByInstitutionAndIdInOrderByInternalIdAsc(institution, subjectIds);
                break;
        }
        return subj.stream()
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
