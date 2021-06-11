package com.fsega.timetable.exception;

import java.util.List;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class CsvException extends RuntimeException {

    private final String message;
    private final List<String> errors;

}
