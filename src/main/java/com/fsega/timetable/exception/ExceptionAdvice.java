package com.fsega.timetable.exception;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;

@ControllerAdvice
public class ExceptionAdvice {

    @ResponseBody
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    public Error notFoundException(BadRequestException e) {
        return new Error(e.getMessage());
    }

    @ResponseBody
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CsvException.class)
    public Error csvException(CsvException e) {
        return new Error(e.getMessage(), e.getErrors());
    }

    @ResponseBody
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public Error notFoundException(NotFoundException e) {
        return new Error(e.getMessage());
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private static class Error {
        private String message;
        private List<String> errors;

        Error(String message) {
            this.message = message;
        }
    }

}
