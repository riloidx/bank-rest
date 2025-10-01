package org.matvey.bankrest.exception;

import org.matvey.bankrest.dto.response.ErrorResponse;
import org.matvey.bankrest.dto.response.ValidationErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Глобальный обработчик исключений для REST API.
 * Перехватывает и обрабатывает различные типы исключений, возвращая структурированные ответы об ошибках.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Создает стандартный ответ об ошибке.
     *
     * @param status HTTP статус ошибки
     * @param error тип ошибки
     * @param message сообщение об ошибке
     * @return ResponseEntity с информацией об ошибке
     */
    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String error, String message) {
        ErrorResponse response = ErrorResponse.builder()
                .status(status.value())
                .error(error)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(status).body(response);
    }

    /**
     * Обрабатывает исключение "Карта не найдена".
     *
     * @param ex исключение CardNotFoundException
     * @return ответ с ошибкой 404
     */
    @ExceptionHandler(CardNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCardNotFoundException(CardNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Карта не найдена", ex.getMessage());
    }

    /**
     * Обрабатывает исключение "Пользователь не найден".
     *
     * @param ex исключение UserNotFoundException
     * @return ответ с ошибкой 404
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Пользователь не найден", ex.getMessage());
    }

    /**
     * Обрабатывает исключение "Роль не найдена".
     *
     * @param ex исключение RoleNotFoundException
     * @return ответ с ошибкой 404
     */
    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRoleNotFoundException(RoleNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Роль не найдена", ex.getMessage());
    }

    /**
     * Обрабатывает исключение "Пользователь уже существует".
     *
     * @param ex исключение UserAlreadyExistsException
     * @return ответ с ошибкой 409
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT, "Пользователь уже существует", ex.getMessage());
    }

    /**
     * Обрабатывает исключение "Недостаточно средств".
     *
     * @param ex исключение InsufficientFundsException
     * @return ответ с ошибкой 400
     */
    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientFundsException(InsufficientFundsException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Недостаточно средств", ex.getMessage());
    }

    /**
     * Обрабатывает исключение "Недопустимая операция с картой".
     *
     * @param ex исключение InvalidCardOperationException
     * @return ответ с ошибкой 400
     */
    @ExceptionHandler(InvalidCardOperationException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCardOperationException(InvalidCardOperationException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Недопустимая операция с картой", ex.getMessage());
    }

    /**
     * Обрабатывает ошибки валидации данных.
     *
     * @param ex исключение MethodArgumentNotValidException
     * @return ответ с детальной информацией об ошибках валидации
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ValidationErrorResponse response = ValidationErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Ошибка валидации")
                .message("Проверьте корректность введенных данных")
                .timestamp(LocalDateTime.now())
                .fieldErrors(errors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Обрабатывает все остальные неперехваченные исключения.
     *
     * @param ex любое исключение
     * @return ответ с общей ошибкой сервера
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Внутренняя ошибка сервера",
                "Произошла неожиданная ошибка");
    }
}
