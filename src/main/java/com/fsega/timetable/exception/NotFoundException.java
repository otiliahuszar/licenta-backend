package com.fsega.timetable.exception;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class NotFoundException extends RuntimeException {

    private final String message;

}
