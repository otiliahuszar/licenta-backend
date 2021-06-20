package com.fsega.timetable.service;

import com.fsega.timetable.exception.NotFoundException;
import com.fsega.timetable.mapper.InstitutionMapper;
import com.fsega.timetable.model.external.IdNameDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.fsega.timetable.model.internal.Institution;
import com.fsega.timetable.repository.InstitutionRepository;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InstitutionService {

    @Value("${institution.name}")
    private String name;

    @Value("${institution.description}")
    private String description;

    @Value("${institution.website}")
    private String website;

    @Value("${institution.foundedIn}")
    private Integer foundedIn;

    private final InstitutionRepository institutionRepository;

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (institutionRepository.findByName(name) != null) {
            return;
        }
        Institution institution = Institution.builder()
                .name(name)
                .description(description)
                .website(website)
                .foundedIn(foundedIn)
                .build();
        institutionRepository.save(institution);
    }

    public Institution getInstitution() {
        return institutionRepository.findByName(name);
    }

    public Institution getInstitution(UUID institutionId) {
        return institutionRepository.findById(institutionId)
                .orElseThrow(() -> new NotFoundException("Institution with id " + institutionId + " was not found"));
    }

    public List<IdNameDto> getInstitutions() {
        return institutionRepository.findByOrderByNameAsc().stream()
                .map(InstitutionMapper::toIdNameDto)
                .collect(Collectors.toList());
    }
}