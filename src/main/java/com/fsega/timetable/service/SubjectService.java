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

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository repository;
    private final InstitutionService institutionService;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public List<IdNameDto> getSubjects(UUID userId, UUID institutionId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " was not found"));

        List<Subject> subj = institutionId == null ?
                searchForDefaultInstitution(user) :
                searchForGivenInstitution(user, institutionId);

        return subj.stream()
                .map(SubjectMapper::toIdNameDto)
                .collect(Collectors.toList());
    }

    private List<Subject> searchForDefaultInstitution(User user) {
        Institution institution = institutionService.getInstitution();

        switch (user.getRole()) {
            case ADMIN:
                return repository.findByInstitutionOrderByInternalIdAsc(institution);
            case TEACHER:
                Set<UUID> subjIds = courseRepository.findAllSubjectIdsForTeacher(user.getId());
                return repository.findAllByInstitutionAndIdInOrderByInternalIdAsc(institution, subjIds);
            case STUDENT:
                Semester sem = user.getSemester().orElse(null);
                if (sem == null) {
                    return new ArrayList<>();
                }
                Set<UUID> subjectIds = courseRepository.findAllSubjectIdsForSpecialization(
                        sem.getSpecialization().getId(), sem.getStudyYear());
                return repository.findAllByInstitutionAndIdInOrderByInternalIdAsc(institution, subjectIds);
            case EXTERNAL_USER:
                return user.getPublicCourses().stream()
                        .map(Course::getSubject)
                        .sorted(Comparator.comparing(Subject::getInternalId))
                        .distinct()
                        .collect(Collectors.toList());
            default:
                return new ArrayList<>();
        }
    }

    private List<Subject> searchForGivenInstitution(User user, UUID institutionId) {
        Institution institution = institutionService.getInstitution(institutionId);

        switch (user.getRole()) {
            case STUDENT:
            case EXTERNAL_USER:
                Set<UUID> subjIds = courseRepository.findAllSubjectIdsForPublicCourses();
                return repository.findAllByInstitutionAndIdInOrderByInternalIdAsc(institution, subjIds);
            default:
                return new ArrayList<>();
        }
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
