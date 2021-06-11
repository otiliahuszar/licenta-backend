package com.fsega.timetable.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fsega.timetable.model.enums.Role;
import com.fsega.timetable.model.internal.User;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmailAndRole(String email, Role role);

    Optional<User> findByUsernameAndRole(String username, Role role);
}
