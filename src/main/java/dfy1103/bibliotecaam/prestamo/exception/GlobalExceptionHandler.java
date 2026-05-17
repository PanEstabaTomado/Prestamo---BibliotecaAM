package dfy1103.bibliotecaam.prestamo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> handleValidationErrors(
            MethodArgumentNotValidException ex){
        // LinkedHashMap mantiene el orden de inserción.
        Map<String, String> errores = new LinkedHashMap<>();
        // getFieldErrors() devuelve uno por cada campo inválido.
        // getField()          → nombre del campo del DTO ("titulo", "precio"...)
        // getDefaultMessage() → el texto del message= en la anotación
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errores.put(error.getField(),error.getDefaultMessage()));

        // 400 Bad Request: el cliente envió datos inválidos.
        return ResponseEntity.badRequest().body(errores);
    }

    // ── ERROR DE NEGOCIO (usuario no encontrada, etc.) ──
    // Se dispara cuando el Service lanza RuntimeException,
    // por ejemplo: "Usuario no encontrada con id: 99"
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntime(RuntimeException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
