package org.spunit.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import org.spunit.common.Constants;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class RestExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(MethodArgumentNotValidException ex, jakarta.servlet.http.HttpServletRequest httpRequest) {
        log.warn("Validation failed: {} violations at path={}", ex.getErrorCount(), httpRequest.getRequestURI());
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Validation failed");
        problem.setType(URI.create(Constants.RFC7807_VALIDATION_TYPE));

        Map<String, String> errors = new HashMap<>();
        for (var error : ex.getBindingResult().getFieldErrors()) {
            String field = error.getField();
            errors.put(field, error.getDefaultMessage());
        }
        problem.setProperty("errors", errors);
        // Standard enrichments
        enrich(problem, httpRequest);
        return new ResponseEntity<>(problem, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ProblemDetail> handleResponseStatus(ResponseStatusException ex, jakarta.servlet.http.HttpServletRequest httpRequest) {
        log.warn("Request failed with status={} reason={} path={}", ex.getStatusCode().value(), ex.getReason(), httpRequest.getRequestURI());
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, ex.getReason());
        // Java 21 switch expression for concise mapping
        String title = switch (status) {
            case NOT_FOUND -> "Resource not found";
            case BAD_REQUEST -> "Bad request";
            case CONFLICT -> "Conflict";
            default -> "Request failed";
        };
        problem.setTitle(title);
        // Enrich
        enrich(problem, httpRequest);
        return ResponseEntity.status(status).body(problem);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGeneric(Exception ex, jakarta.servlet.http.HttpServletRequest httpRequest) {
        log.error("Unexpected error at path={}", httpRequest.getRequestURI(), ex);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error");
        problem.setTitle("Internal server error");
        enrich(problem, httpRequest);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem);
    }

    private void enrich(ProblemDetail problem, jakarta.servlet.http.HttpServletRequest httpRequest) {
        problem.setProperty(Constants.PROP_TIMESTAMP, java.time.Instant.now());
        problem.setInstance(URI.create(httpRequest.getRequestURI()));
        String requestId = (String) httpRequest.getAttribute(Constants.REQUEST_ID_ATTRIBUTE);
        if (requestId != null) {
            problem.setProperty(Constants.PROP_REQUEST_ID, requestId);
        }
    }
}
