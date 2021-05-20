package com.fsega.timetable.exception;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class UnauthorizedException extends RuntimeException {

    private final String message;

}