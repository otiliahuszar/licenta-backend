package com.fsega.timetable.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.fsega.timetable.model.internal.Institution;
import com.fsega.timetable.repository.InstitutionRepository;

import lombok.RequiredArgsConstructor;

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
}