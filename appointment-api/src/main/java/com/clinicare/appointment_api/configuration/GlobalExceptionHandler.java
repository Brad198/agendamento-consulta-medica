package com.clinicare.appointment_api.configuration;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.clinicare.appointment_api.shared.exception.BussinessException;
import com.clinicare.appointment_api.shared.exception.ForbiddenActionException;
import com.clinicare.appointment_api.shared.exception.ResourceAlreadyExistsException;
import com.clinicare.appointment_api.shared.exception.ResourceNotFoundException;
import com.clinicare.appointment_api.shared.utils.CustomResponseError;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final Map<Class<? extends BussinessException>, HttpStatus> mapStatusCode;

    public GlobalExceptionHandler() {
        this.mapStatusCode = Map.of(
                ForbiddenActionException.class, HttpStatus.FORBIDDEN,
                ResourceAlreadyExistsException.class, HttpStatus.CONFLICT,
                ResourceNotFoundException.class, HttpStatus.NOT_FOUND);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            @NonNull MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {

        Map<String, String> errorFields = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(
                        Collectors.toMap(
                                FieldError::getField,
                                field -> Optional.ofNullable(field.getDefaultMessage()).orElse("Erro não informado"),
                                (existing, replacement) -> existing));

        ex.getBindingResult().getGlobalErrors().forEach(globalError -> {
            String key = globalError.getObjectName();
            errorFields.put(key,
                    Optional.ofNullable(globalError.getDefaultMessage()).orElse("Erro não informado"));
        });

        var error = new CustomResponseError<>(
                status.value(),
                HttpStatus.BAD_REQUEST.name(),
                LocalDateTime.now(),
                List.of(errorFields));

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(BussinessException.class)
    public ResponseEntity<CustomResponseError<String>> handleBussinessException(BussinessException ex) {
        HttpStatus statusCode = this.mapStatusCode.get(ex.getClass());
        CustomResponseError<String> error = new CustomResponseError<>(
                statusCode.value(),
                statusCode.name(),
                LocalDateTime.now(),
                Optional.ofNullable(ex.getMessage())
                        .orElse("Erro interno não informado"));

        return ResponseEntity.status(error.status()).body(error);
    }
}
