package com.fsega.timetable.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fsega.timetable.model.internal.User;

public interface UserRepository extends JpaRepository<User, UUID> {
}
