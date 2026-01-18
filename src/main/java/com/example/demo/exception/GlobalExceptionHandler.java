package com.example.demo.exception;

import com.example.demo.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError; // Import
import org.springframework.web.bind.MethodArgumentNotValidException; // Import
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();

        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        ErrorResponse errorResponse = new ErrorResponse(
                "Veri doğrulama hatası",
                HttpStatus.BAD_REQUEST.value(),
                System.currentTimeMillis(),
                errors
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // 2. Mantıksal Hatalar (Örn: Koltuk dolu)
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException e) {
        ErrorResponse error = new ErrorResponse(
                e.getMessage(), // Servis'ten gelen mesajı kullanıcıya gösteriyoruz
                HttpStatus.CONFLICT.value(), // 409 Conflict (Çakışma)
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    // 3. Genel Beklenmeyen Hatalar (Zırhın son katmanı)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception e) {
        log.error("BEKLENMEYEN HATA: ", e);

        ErrorResponse error = new ErrorResponse(
                "Sunucuda beklenmeyen bir hata oluştu. Lütfen daha sonra tekrar deneyiniz.",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                System.currentTimeMillis()
        );
        // İPUCU: e.getMessage() detayını güvenlik için kullanıcıya dönmüyoruz!
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}