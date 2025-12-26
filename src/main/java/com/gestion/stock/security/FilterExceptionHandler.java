package com.gestion.stock.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestion.stock.exception.DuplicateResourceException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class FilterExceptionHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void handleException(HttpServletResponse response, Exception ex) throws IOException {
        HttpStatus status = determineHttpStatus(ex);
        
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        if (ex instanceof MethodArgumentNotValidException validationEx) {
            Map<String, String> errors = new HashMap<>();
            validationEx.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
            );
            response.getWriter().write(objectMapper.writeValueAsString(errors));
        } else {
            response.getWriter().write(ex.getMessage());
        }
    }

    private HttpStatus determineHttpStatus(Exception ex) {
        if (ex instanceof IllegalArgumentException) {
            return HttpStatus.BAD_REQUEST;
        } else if (ex instanceof DataIntegrityViolationException) {
            return HttpStatus.BAD_REQUEST;
        } else if (ex instanceof MethodArgumentNotValidException) {
            return HttpStatus.BAD_REQUEST;
        } else if (ex instanceof DuplicateResourceException) {
            return HttpStatus.BAD_REQUEST;
        } else if (ex instanceof EntityNotFoundException) {
            return HttpStatus.NOT_FOUND;
        } else if (ex instanceof AccessDeniedException) {
            return HttpStatus.FORBIDDEN;
        } else if (ex instanceof UsernameNotFoundException) {
            return HttpStatus.UNAUTHORIZED;
        } else if (ex instanceof BadCredentialsException) {
            return HttpStatus.UNAUTHORIZED;
        } else if (ex instanceof DisabledException) {
            return HttpStatus.FORBIDDEN;
        } else if (ex instanceof LockedException) {
            return HttpStatus.FORBIDDEN;
        } else {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}