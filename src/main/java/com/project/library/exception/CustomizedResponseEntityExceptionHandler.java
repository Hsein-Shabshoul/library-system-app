package com.project.library.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

//Log4j2
@RestControllerAdvice
public class CustomizedResponseEntityExceptionHandler {


    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ExceptionResponse> handleAllExceptions(Exception exception,
                                                                       WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(LocalDateTime.now(), exception.getMessage(),
                request.getDescription(false),HttpStatus.BAD_REQUEST,HttpStatus.BAD_REQUEST.value());
        //log.warn(exceptionResponse);
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }
    //interpolated message in e.getConstraintViolations
@ExceptionHandler(ConstraintViolationException.class)
@ResponseStatus(HttpStatus.BAD_REQUEST)
ValidationErrorResponse onNotValidException(ConstraintViolationException e) {
    ValidationErrorResponse error = new ValidationErrorResponse();
    Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
    for (ConstraintViolation<?> fieldError : constraintViolations) {
        error.getViolations().add(new Violation(fieldError.getInvalidValue().toString(), fieldError.getMessage()));
    }
    //log.info(error);
    return error;
}
@ExceptionHandler(ResourceNotFoundException.class)
public final ResponseEntity<ExceptionResponse> handleNotFoundExceptions(ResourceNotFoundException exception,
                                                                       WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(LocalDateTime.now(), exception.getMessage(),
                request.getDescription(false),HttpStatus.BAD_REQUEST,HttpStatus.BAD_REQUEST.value());
        //log.warn(exceptionResponse);

        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadRequestException.class)
    public final ResponseEntity<ExceptionResponse> handleUserNotFoundException(BadRequestException badRequestException,
                                                                               WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(LocalDateTime.now(),
                badRequestException.getMessage(),
                request.getDescription(false),HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value());
        //log.warn(exceptionResponse);
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsE.class)
    public final ResponseEntity<ExceptionResponse> handleBadCredentials(BadCredentialsE e,
                                                                               WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(LocalDateTime.now(),
                e.getMessage(),
                request.getDescription(false),HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value());
        //log.warn(exceptionResponse);

        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    @ResponseBody
//    public final ResponseEntity<ExceptionResponse> onMethodNotValidException(MethodArgumentNotValidException e,
//                                                                             WebRequest request) {
//        ExceptionResponse exceptionResponse = new ExceptionResponse(LocalDateTime.now(),
//                e.getMessage(),
//                request.getDescription(false),HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value());
//
//        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
//    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody

    ValidationErrorResponse onMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        ValidationErrorResponse error = new ValidationErrorResponse();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            error.getViolations().add(new Violation(fieldError.getField(), fieldError.getDefaultMessage()));
        }
        //log.info(error);
        return error;
    }


//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<Map<String, List<String>>> handleValidationErrors(MethodArgumentNotValidException ex) {
//        List<String> errors = ex.getBindingResult().getFieldErrors()
//                .stream().map(FieldError::getDefaultMessage).collect(Collectors.toList());
//        return new ResponseEntity<>(getErrorsMap(errors), new HttpHeaders(), HttpStatus.BAD_REQUEST);
//    }
    private Map<String, List<String>> getErrorsMap(List<String> errors) {
        Map<String, List<String>> errorResponse = new HashMap<>();
        errorResponse.put("Errors", errors);
        return errorResponse;
    }
}
