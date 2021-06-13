package com.fsega.timetable.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.fsega.timetable.model.external.IdNameDto;
import com.fsega.timetable.model.internal.Institution;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fsega.timetable.config.ldap.LdapUser;
import com.fsega.timetable.config.ldap.LdapUserRepository;
import com.fsega.timetable.exception.BadRequestException;
import com.fsega.timetable.exception.NotFoundException;
import com.fsega.timetable.mapper.UserMapper;
import com.fsega.timetable.model.enums.Role;
import com.fsega.timetable.model.external.UserCreateDto;
import com.fsega.timetable.model.external.UserDto;
import com.fsega.timetable.model.internal.Semester;
import com.fsega.timetable.model.internal.User;
import com.fsega.timetable.repository.SemesterRepository;
import com.fsega.timetable.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final LdapUserRepository ldapUserRepository;
    private final SemesterRepository semesterRepository;
    private final PasswordEncoder encoder;
    private final InstitutionService institutionService;

    public UserDto getUser(UUID id) {
        return userRepository.findById(id)
                .map(UserMapper::toDto)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " was not found"));
    }

    public User createLdapUser(LdapUser ldapUser, String password) {
        Institution institution = institutionService.getInstitution();
        User user = userRepository.findByUsernameAndRole(ldapUser.getUsername(), ldapUser.getRole())
                .orElseGet(() -> UserMapper.toEntity(ldapUser));

        user.setPassword(encoder.encode(password));
        user.setInstitution(institution);
        setSemester(user, ldapUser);

        return userRepository.save(user);
    }

    private void setSemester(User user, LdapUser ldapUser) {
        if (user.getRole() == Role.STUDENT) {
            Semester semester = semesterRepository.findByStudyYearAndSpecialization_InternalId(
                    ldapUser.getStudyYear(), ldapUser.getSpecializationId());
            user.addSemester(semester);
        }
    }

    public UserDto createExternalUser(UserCreateDto dto) {
        validateUsername(dto.getUsername());
        validateEmail(dto.getEmail());

        User s = UserMapper.toEntity(dto);
        s.setPassword(encoder.encode(dto.getPassword()));
        s.setRole(Role.EXTERNAL_USER);

        User user = userRepository.save(s);
        return UserMapper.toDto(user);
    }

    private void validateUsername(String username) {
        userRepository.findByUsernameAndRole(username, Role.EXTERNAL_USER)
                .ifPresent(u -> {
                    throw new BadRequestException("User with username " + username + " already exists");
                });
    }

    private void validateEmail(String email) {
        userRepository.findByEmailAndRole(email, Role.EXTERNAL_USER)
                .ifPresent(u -> {
                    throw new BadRequestException("User with email " + email + " already exists");
                });
    }

    public Optional<User> createTeacher(String username) {
        return ldapUserRepository.findTeacherByUsername(username)
                .map(this::createTeacher);
    }

    private User createTeacher(LdapUser ldapTeacher) {
        return userRepository.findByUsernameAndRole(ldapTeacher.getUsername(), Role.TEACHER)
                .orElseGet(() -> {
                    User teacher = UserMapper.toEntity(ldapTeacher);

                    Institution institution = institutionService.getInstitution();
                    teacher.setInstitution(institution);
                    return userRepository.save(teacher);
                });
    }

    public List<IdNameDto> getTeachers() {
        Institution institution = institutionService.getInstitution();

        return userRepository.findByRoleAndInstitutionOrderByLastNameAscFirstNameAsc(Role.TEACHER, institution).stream()
                .map(UserMapper::toIdNameDto)
                .collect(Collectors.toList());
    }

}
