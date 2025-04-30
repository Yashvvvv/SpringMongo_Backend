package com.example.spring_boot_crash_course

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * Handles global validation errors in the application.
 * This class is annotated with RestControllerAdvice, indicating
 * it applies to all controllers.
 */
@RestControllerAdvice
class GlobalValidationHandler {

    /**
     * Handles validation errors thrown when method arguments fail validation constraints.
     *
     * @param e the exception containing information about validation errors
     * @return a ResponseEntity containing a map with a list of error messages and an HTTP 400 status code
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationError(e: MethodArgumentNotValidException): ResponseEntity<Map<String, Any>> {
        val errors = e.bindingResult.allErrors.map{
            it.defaultMessage ?: "Invalid value"
        }
        return ResponseEntity
            .status(400)
            .body(mapOf("errors" to errors))
    }

}