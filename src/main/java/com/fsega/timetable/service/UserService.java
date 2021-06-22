package com.fsega.timetable.service;

import java.util.*;
import java.util.stream.Collectors;

import com.fsega.timetable.model.external.IdNameDto;
import com.fsega.timetable.model.external.PasswordUpdateDto;
import com.fsega.timetable.model.internal.*;
import com.fsega.timetable.repository.CourseRepository;
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
    private final CourseRepository courseRepository;

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
            semesterRepository.findByStudyYearAndSpecialization_InternalId(ldapUser.getStudyYear(), ldapUser.getSpecializationId())
                    .ifPresent(user::addSemester);
        }
    }

    public UserDto createExternalUser(UserCreateDto dto) {
        validateUsername(dto.getUsername(), Role.EXTERNAL_USER);
        validateEmail(dto.getEmail(), Role.EXTERNAL_USER);

        User s = UserMapper.toEntity(dto);
        s.setPassword(encoder.encode(dto.getPassword()));
        s.setRole(Role.EXTERNAL_USER);

        User user = userRepository.save(s);
        return UserMapper.toDto(user);
    }

    public UserDto updateUser(UUID userId, UserCreateDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " was not found"));
        if (!user.getEmail().equals(dto.getEmail())) {
            validateEmail(dto.getEmail(), user.getRole());
        }
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setReceiveEmailNotificationsBeforeCourses(dto.isReceiveEmailNotificationsBeforeCourses());
        user.setNotificationInterval(dto.getNotificationInterval());
        user.setReceiveEmailNotificationsForUpdates(dto.isReceiveEmailNotificationsForUpdates());

        return UserMapper.toDto(userRepository.save(user));
    }

    public boolean updatePassword(UUID userId, PasswordUpdateDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " was not found"));

        if (!encoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new BadRequestException("Your old password is incorrect");
        }
        if (dto.getNewPassword().equals(dto.getOldPassword())) {
            throw new BadRequestException("Your new password cannot be the same as the old one");
        }
        user.setPassword(encoder.encode(dto.getNewPassword()));
        userRepository.save(user);
        return true;
    }

    private void validateUsername(String username, Role role) {
        userRepository.findByUsernameAndRole(username, role)
                .ifPresent(u -> {
                    throw new BadRequestException("User with username " + username + " already exists");
                });
    }

    private void validateEmail(String email, Role role) {
        userRepository.findByEmailAndRole(email, role)
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

    public List<IdNameDto> getTeachers(UUID userId, UUID institutionId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " was not found"));

        List<User> teachers = institutionId == null ?
                searchForDefaultInstitution(user) :
                searchForGivenInstitution(user, institutionId);

        return teachers.stream()
                .map(UserMapper::toIdNameDto)
                .collect(Collectors.toList());
    }

    private List<User> searchForDefaultInstitution(User user) {
        Institution institution = institutionService.getInstitution();

        switch (user.getRole()) {
            case ADMIN:
                return userRepository.findByRoleAndInstitutionOrderByLastNameAscFirstNameAsc(Role.TEACHER, institution);
            case STUDENT:
                Semester sem = user.getSemester().orElse(null);
                if (sem == null) {
                    return new ArrayList<>();
                }
                Set<UUID> teacherIds = courseRepository.findAllTeacherIdsForSpecialization(
                        sem.getSpecialization().getId(), sem.getStudyYear());
                return userRepository.findByInstitutionAndIdInOrderByLastNameAscFirstNameAsc(institution, teacherIds);
            case EXTERNAL_USER:
                return user.getPublicCourses().stream()
                        .map(Course::getTeacher)
                        .sorted(Comparator.comparing(User::getFullName))
                        .distinct()
                        .collect(Collectors.toList());
            default:
                return new ArrayList<>();
        }
    }

    private List<User> searchForGivenInstitution(User user, UUID institutionId) {
        Institution institution = institutionService.getInstitution(institutionId);

        switch (user.getRole()) {
            case STUDENT:
            case EXTERNAL_USER:
                Set<UUID> teacherIds = courseRepository.findAllTeacherIdsForPublicCourses();
                return userRepository.findByInstitutionAndIdInOrderByLastNameAscFirstNameAsc(institution, teacherIds);
            default:
                return new ArrayList<>();
        }
    }

}
