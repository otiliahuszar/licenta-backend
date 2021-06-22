package com.fsega.timetable.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import com.fsega.timetable.model.internal.Institution;
import org.springframework.data.jpa.repository.JpaRepository;

import com.fsega.timetable.model.enums.Role;
import com.fsega.timetable.model.internal.User;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmailAndRole(String email, Role role);

    Optional<User> findByUsernameAndRole(String username, Role role);

    List<User> findByRoleAndInstitutionOrderByLastNameAscFirstNameAsc(Role role, Institution institution);

    List<User> findByInstitutionAndIdInOrderByLastNameAscFirstNameAsc(Institution institution, Set<UUID> ids);

    Set<User> findAllByReceiveEmailNotificationsBeforeCourses(boolean receive);
}
