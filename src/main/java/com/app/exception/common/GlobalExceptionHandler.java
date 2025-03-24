package com.app.exception.common;

import com.app.config.LoggerService;
import com.app.exception.custom.InvalidParamException;
import com.app.exception.custom.UserAlreadyExistException;
import com.app.exception.custom.UserNotFoundException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;



@RestControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private LoggerService logger;
    private final String TAG = "GlobalExceptionHandler";

    /**
     * Handles validation exceptions from @Valid in request bodies.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        logger.warn(TAG+" Validation exception: {}", ex.getMessage());
        String errorMessage = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));

        return buildResponse(HttpStatus.BAD_REQUEST, errorMessage);
    }

    /**
     * Handles @Valid constraint violations from request parameters.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex) {
        logger.warn(TAG+" Constraint violation: {}", ex.getMessage());
        String errorMessage = ex.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));

        return buildResponse(HttpStatus.BAD_REQUEST, errorMessage);
    }

    /**
     * Handles invalid request method exceptions.
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Object> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        logger.warn(TAG+" Method not allowed: {}", ex.getMessage());
        String message = "Request method '" + ex.getMethod() + "' is not supported for this endpoint.";
        return buildResponse(HttpStatus.METHOD_NOT_ALLOWED, message);
    }

    /**
     * Handles invalid endpoint exceptions.
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        logger.warn(TAG+" No endpoint found: {}", ex.getRequestURL());
        return buildResponse(HttpStatus.NOT_FOUND, "The requested endpoint does not exist: " + ex.getRequestURL());
    }


    /**
     * Handles invalid parameter exceptions.
     */
    @ExceptionHandler(InvalidParamException.class)
    public ResponseEntity<Object> handleInvalidParamException(InvalidParamException ex) {
        logger.warn(TAG+" Invalid parameter: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * Handles user already exists exception.
     */
    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<Object> handleUserAlreadyExistException(UserAlreadyExistException ex) {
        logger.warn(TAG+" User already exists: {}", ex.getMessage());
        return buildResponse(HttpStatus.CONFLICT, "User already exists");
    }

    /**
     * Handles user not found exceptions.
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException ex) {
        logger.warn(TAG+" User not found: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * Handles database unique constraint violations (e.g., duplicate email).
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDuplicateKeyException(DataIntegrityViolationException ex) {
        logger.error(TAG+" Database constraint violation: {}", ex.getMessage(),ex);

        String errorMessage = "Duplicate entry";
        if (ex.getCause() != null && ex.getCause().getMessage() != null) {
            String causeMessage = ex.getCause().getMessage();
            if (causeMessage.contains("Duplicate entry")) {
                errorMessage = causeMessage.split(" for key ")[0]; // Extract relevant part
            }
        }

        return buildResponse(HttpStatus.CONFLICT, errorMessage);
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleJsonParseError(HttpMessageNotReadableException ex) {
        logger.error(TAG+" Invalid JSON received: {}", ex.getMessage(), ex);
        return buildResponse(HttpStatus.BAD_REQUEST, "Invalid JSON format. Please check your request body.");
    }


    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Object> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException ex) {
        logger.error(TAG+" Unsupported Content-Type: {}", ex.getMessage(), ex);
        return buildResponse(HttpStatus.BAD_REQUEST, "Unsupported Content-Type. Please use application/json.");
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<Object> handleLockedException(LockedException ex) {
        logger.error(TAG+" Account locked: {}", ex.getMessage(), ex);
        return buildResponse(HttpStatus.LOCKED, "Account locked");
    }


    /**
     * Handles all unexpected exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneralException(Exception ex) {
        logger.error(TAG+" Unexpected error: {}", ex.getMessage(), ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong. Please try again later.");
    }

    /**
     * Handles bad credentials exceptions.
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredentialsException(BadCredentialsException ex) {
        logger.error(TAG+" Bad Credentials : {}", ex.getMessage(), ex);
        return buildResponse(HttpStatus.UNAUTHORIZED, "The username or password is incorrect");
    }

    /**
     * Handles account locked exceptions.
     */
    @ExceptionHandler(AccountStatusException.class)
    public ResponseEntity<Object> handleAccountStatusException(AccountStatusException ex) {
        logger.error(TAG+" Account locked : {}", ex.getMessage(), ex);
        return buildResponse(HttpStatus.FORBIDDEN, "The account is locked");
    }


    /**
     * Handles access denied exceptions.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex) {
        logger.error(TAG+" Access Denied : {}", ex.getMessage(), ex);
        return buildResponse(HttpStatus.FORBIDDEN, "You are not authorized to access this resource");
    }

    /**
     * Handles invalid signature exceptions.
     */
    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<Object> handleSignatureException(SignatureException ex) {
        logger.error(TAG+" Invalid signature : {}", ex.getMessage(), ex);
        return buildResponse(HttpStatus.FORBIDDEN, "The JWT signature is invalid");
    }

    /**
     * Handles expired jwt token exceptions.
     */
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<Object> handleExpiredJwtException(SignatureException ex) {
        logger.error(TAG+" Expired Jwt token : {}", ex.getMessage(), ex);
        return buildResponse(HttpStatus.FORBIDDEN, "The JWT token has expired");
    }

    /**
     * Helper method to create a consistent error response.
     */
    private ResponseEntity<Object> buildResponse(HttpStatus status, String message) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", new Date());
        errorResponse.put("status_code", status.value());
        errorResponse.put("success", false);
        errorResponse.put("data", null);
        errorResponse.put("message", message);

        return new ResponseEntity<>(errorResponse, status);
    }
}

