package com.fsega.timetable.service;

import com.fsega.timetable.config.security.UserDetails;
import com.fsega.timetable.exception.NotFoundException;
import com.fsega.timetable.model.external.IdNameDto;
import com.fsega.timetable.model.internal.User;
import com.fsega.timetable.repository.CourseRepository;
import com.fsega.timetable.repository.UserRepository;
import org.springframework.stereotype.Service;

import com.fsega.timetable.mapper.SpecializationMapper;
import com.fsega.timetable.model.external.SpecializationDto;
import com.fsega.timetable.model.internal.Institution;
import com.fsega.timetable.model.internal.Specialization;
import com.fsega.timetable.repository.SpecializationRepository;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpecializationService {

    private final SpecializationRepository repository;
    private final InstitutionService institutionService;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public List<IdNameDto> getSpecializations(UUID userId) {
        Institution institution = institutionService.getInstitution();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " was not found"));
        List<Specialization> spec = new ArrayList<>();

        switch (user.getRole()) {
            case ADMIN:
                spec = repository.findByInstitutionOrderByInternalIdAsc(institution);
                break;
            case TEACHER:
                Set<UUID> specIds = courseRepository.findSpecializationIdsForTeacher(user.getId());
                spec = repository.findAllByInstitutionAndIdInOrderByInternalIdAsc(institution, specIds);
                break;
        }
        return spec.stream()
                .map(SpecializationMapper::toIdNameDto)
                .collect(Collectors.toList());
    }

    Specialization createSpecialization(SpecializationDto dto) {
        Institution institution = institutionService.getInstitution();

        return repository.findByInternalIdAndInstitution(dto.getInternalId(), institution)
                .orElseGet(() -> {
                    Specialization s = SpecializationMapper.toEntity(dto, institution);
                    return repository.save(s);
                });
    }
}
