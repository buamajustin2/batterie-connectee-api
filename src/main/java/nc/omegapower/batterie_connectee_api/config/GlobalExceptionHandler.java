package nc.omegapower.batterie_connectee_api.config;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** Erreurs de type de paramètre (ex: date mal formée). */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {

        String message = String.format(
                "Paramètre '%s' invalide : '%s' n'est pas du type attendu (%s)",
                ex.getName(),
                ex.getValue(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "?");

        log.warn("400 - {} - path={}", message, request.getRequestURI());

        return ResponseEntity.badRequest().body(errorBody(
                HttpStatus.BAD_REQUEST, message, request.getRequestURI()));
    }

    /** Erreurs de parsing de date (fallback). */
    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<Map<String, Object>> handleDateTimeParse(
            DateTimeParseException ex,
            HttpServletRequest request) {

        String message = "Format de date invalide : " + ex.getParsedString()
                + " (attendu : yyyy-MM-dd ou yyyy-MM-ddTHH:mm:ss)";

        log.warn("400 - {} - path={}", message, request.getRequestURI());

        return ResponseEntity.badRequest().body(errorBody(
                HttpStatus.BAD_REQUEST, message, request.getRequestURI()));
    }

    /** Erreurs métier remontées par les services. */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        log.warn("400 - {} - path={}", ex.getMessage(), request.getRequestURI());

        return ResponseEntity.badRequest().body(errorBody(
                HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI()));
    }

    /** Erreurs de validation @Valid. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        Map<String, String> champs = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(err ->
                champs.put(err.getField(), err.getDefaultMessage()));

        Map<String, Object> body = errorBody(
                HttpStatus.BAD_REQUEST, "Validation échouée", request.getRequestURI());
        body.put("champs", champs);

        return ResponseEntity.badRequest().body(body);
    }

    /** Fallback : tout le reste → 500. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(
            Exception ex,
            HttpServletRequest request) {

        log.error("500 - {} - path={}", ex.getMessage(), request.getRequestURI(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorBody(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Erreur interne : " + ex.getMessage(),
                request.getRequestURI()));
    }

    // ------------------------------------------------------------------

    private Map<String, Object> errorBody(HttpStatus status, String message, String path) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("path", path);
        return body;
    }
}