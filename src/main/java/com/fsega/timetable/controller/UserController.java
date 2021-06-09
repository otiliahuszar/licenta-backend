package com.fsega.timetable.controller;

import java.util.UUID;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.fsega.timetable.model.external.UserCreateDto;
import com.fsega.timetable.model.external.UserDto;
import com.fsega.timetable.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto createUser(@RequestBody @Valid UserCreateDto userCreateDto) {
        return userService.createExternalUser(userCreateDto);
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@RequestHeader(name = "Authorization") String token,
                               @PathVariable UUID userId) {
        return userService.getUser(userId);
    }
}
