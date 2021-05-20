package com.fsega.timetable.controller;

import java.util.UUID;

import org.springframework.web.bind.annotation.*;

import com.fsega.timetable.model.external.UserDto;
import com.fsega.timetable.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable UUID userId) {
        return userService.getUser(userId);
    }
}
