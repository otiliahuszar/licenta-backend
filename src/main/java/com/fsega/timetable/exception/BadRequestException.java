package com.fsega.timetable.exception;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class BadRequestException extends RuntimeException {

    private final String message;

}
