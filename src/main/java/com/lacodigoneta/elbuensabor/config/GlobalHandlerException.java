package com.lacodigoneta.elbuensabor.config;

import com.lacodigoneta.elbuensabor.exceptions.Error;
import com.lacodigoneta.elbuensabor.exceptions.*;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static com.lacodigoneta.elbuensabor.config.AppConstants.INVALID_CREDENTIALS;

@RestControllerAdvice
public class GlobalHandlerException extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {
            CategoryException.class,
            IncompatibleMeasurementUnitTypeException.class,
            IngredientException.class,
            IngredientPurchaseException.class,
            InvalidCredentialsException.class,
            InvalidParentException.class,
            InvalidTokenException.class,
            IllegalArgumentException.class,
            NoLoggedUserException.class,
            NotAllowedOperationException.class,
            PermissionsException.class,
            ProductException.class,
            AccessDeniedException.class,
            ExpiredJwtException.class,
            BadCredentialsException.class
    })
    public ResponseEntity<?> handleExceptions(Exception ex) {
        Error errorResponse = Error.builder().message(ex.getMessage()).build();
        HttpStatus status = HttpStatus.BAD_REQUEST;
        if (ex instanceof AccessDeniedException ||
                ex instanceof PermissionsException ||
                ex instanceof NoLoggedUserException) {
            status = HttpStatus.FORBIDDEN;
        }
        if(ex instanceof BadCredentialsException){
            errorResponse.setMessage(INVALID_CREDENTIALS);
        }
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler({Exception.class, RuntimeException.class})
    public ResponseEntity<?> handleRuntimeException(Exception exception) {

        String message = "";
        try {
            message = exception.getCause().getMessage();
        } catch (NullPointerException e) {
            message = exception.getMessage();
        }
        Error errorResponse = Error.builder().message(message).build();
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }


    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, @NotNull HttpHeaders headers, @NotNull HttpStatusCode status, @NotNull WebRequest request) {
        String message = "";
        try {
            message = ex.getMessage()
                    .replace("JSON parse error: ", "")
                    .replace(ex.getMessage().substring(ex.getMessage().indexOf("of type"),
                                    ex.getMessage().indexOf("from"))
                            , "")
                    .replace("\"", "'");
        } catch (IndexOutOfBoundsException e) {
            message = ex.getMessage().substring(0, ex.getMessage().indexOf(":"));
        }
        Error errorResponse = Error.builder().message(message).build();
        return new ResponseEntity<>(errorResponse, headers, HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, @NotNull HttpHeaders headers, @NotNull HttpStatusCode status, @NotNull WebRequest request) {

        Error errorResponse = new Error();

        ex.getFieldErrors().forEach(e -> {
            errorResponse.getErrors().put(e.getField(), e.getDefaultMessage());
        });

        ex.getGlobalErrors().forEach(e -> {
            errorResponse.getErrors().put("request", e.getDefaultMessage());
        });
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
