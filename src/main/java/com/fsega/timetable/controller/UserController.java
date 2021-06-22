package com.fsega.timetable.controller;

import java.util.UUID;

import javax.validation.Valid;

import com.fsega.timetable.model.external.PasswordUpdateDto;
import org.springframework.web.bind.annotation.*;

import com.fsega.timetable.model.external.UserCreateDto;
import com.fsega.timetable.model.external.UserDto;
import com.fsega.timetable.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto createUser(@RequestBody @Valid UserCreateDto dto) {
        return userService.createExternalUser(dto);
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable UUID userId) {
        return userService.getUser(userId);
    }

    @PutMapping("/{userId}")
    public UserDto updateUser(@PathVariable UUID userId,
                              @RequestBody @Valid UserCreateDto dto) {
        return userService.updateUser(userId, dto);
    }

    @PutMapping("/{userId}/password")
    public boolean updateUserPassword(@PathVariable UUID userId,
                                      @RequestBody @Valid PasswordUpdateDto dto) {
        return userService.updatePassword(userId, dto);
    }
}
