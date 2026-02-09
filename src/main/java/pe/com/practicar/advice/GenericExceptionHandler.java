package pe.com.practicar.advice;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.codec.DecodingException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ServerWebInputException;
import pe.com.practicar.business.dto.CustomErrorResponse;
import pe.com.practicar.business.exception.BusinessErrorCodes;
import pe.com.practicar.business.exception.BusinessException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Manejador global de excepciones para la aplicación.
 * Captura y procesa diferentes tipos de excepciones para devolver respuestas de error estandarizadas.
 */
@RestControllerAdvice
@Slf4j
public class GenericExceptionHandler {

    /**
     * Maneja errores de tipo de parámetros en WebFlux.
     * Se activa cuando se envía un valor de tipo incorrecto (ej: "1a" en lugar de 1 para un Integer).
     */
    @ExceptionHandler(ServerWebInputException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CustomErrorResponse handleServerWebInputException(ServerWebInputException ex) {
        log.error("Error de entrada web: {}", ex.getMessage(), ex);
        
        String mensaje = "Formato de parámetro inválido. Verifique que los valores enviados sean correctos.";
        
        Throwable cause = ex.getCause();
        while (cause != null) {
            String causeMessage = cause.getMessage();
            if (causeMessage != null) {
                if (causeMessage.contains("Cannot deserialize value of type")) {
                    if (causeMessage.contains("field \"")) {
                        int fieldStart = causeMessage.indexOf("field \"") + 7;
                        int fieldEnd = causeMessage.indexOf("\"", fieldStart);
                        if (fieldStart > 6 && fieldEnd > fieldStart) {
                            String fieldName = causeMessage.substring(fieldStart, fieldEnd);
                            mensaje = String.format("El campo '%s' tiene un formato inválido. Verifique el tipo de dato.", fieldName);
                            break;
                        }
                    }
                    if (causeMessage.contains("from String \"")) {
                        int valueStart = causeMessage.indexOf("from String \"") + 13;
                        int valueEnd = causeMessage.indexOf("\"", valueStart);
                        if (valueStart > 12 && valueEnd > valueStart) {
                            String invalidValue = causeMessage.substring(valueStart, valueEnd);
                            mensaje = String.format("El valor '%s' no es válido. Verifique el tipo de dato esperado.", invalidValue);
                            break;
                        }
                    }
                    mensaje = "Uno o más campos tienen un formato de dato inválido.";
                    break;
                }
                
                if (causeMessage.contains("For input string:")) {
                    int startIdx = causeMessage.indexOf("For input string: \"") + 19;
                    int endIdx = causeMessage.indexOf("\"", startIdx);
                    if (startIdx > 18 && endIdx > startIdx) {
                        String invalidValue = causeMessage.substring(startIdx, endIdx);
                        mensaje = String.format("El valor '%s' no es válido. Se esperaba un número entero.", invalidValue);
                        break;
                    }
                } else if (causeMessage.contains("java.lang.Integer")) {
                    mensaje = "Se esperaba un número entero válido.";
                    break;
                }
            }
            cause = cause.getCause();
        }
        
        CustomErrorResponse.ErrorDetail errorDetail = CustomErrorResponse.ErrorDetail.builder()
                .tipo("FUNCIONAL")
                .codigo(BusinessErrorCodes.INVALID_PARAMETER_TYPE.getCode())
                .mensaje(mensaje)
                .build();
        
        return CustomErrorResponse.builder()
                .error(errorDetail)
                .build();
    }

    /**
     * Maneja errores de tipo de parámetros en Spring MVC tradicional (fallback).
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CustomErrorResponse handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.error("Error de tipo de parámetro: {}", ex.getMessage(), ex);
        
        String paramName = ex.getName();
        String requiredType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "desconocido";
        Object value = ex.getValue();
        
        String mensaje = String.format(
            "El parámetro '%s' con valor '%s' no es válido. Se espera un tipo %s.",
            paramName, value, requiredType
        );
        
        CustomErrorResponse.ErrorDetail errorDetail = CustomErrorResponse.ErrorDetail.builder()
                .tipo("FUNCIONAL")
                .codigo(BusinessErrorCodes.INVALID_PARAMETER_TYPE.getCode())
                .mensaje(mensaje)
                .build();
        
        return CustomErrorResponse.builder()
                .error(errorDetail)
                .build();
    }

    /**
     * Maneja violaciones de restricciones de validación (@Valid, @Validated).
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CustomErrorResponse handleConstraintViolation(ConstraintViolationException ex) {
        log.error("Error de validación de restricciones: {}", ex.getMessage(), ex);
        
        List<ConstraintViolation<?>> violations = new ArrayList<>(ex.getConstraintViolations());
        
        String mensaje = violations.stream()
                .map(violation -> String.format("%s: %s", 
                    violation.getPropertyPath(), 
                    violation.getMessage()))
                .collect(Collectors.joining(", "));
        
        CustomErrorResponse.ErrorDetail errorDetail = CustomErrorResponse.ErrorDetail.builder()
                .tipo("FUNCIONAL")
                .codigo(BusinessErrorCodes.CONSTRAINT_VIOLATION.getCode())
                .mensaje("Errores de validación: " + mensaje)
                .build();
        
        return CustomErrorResponse.builder()
                .error(errorDetail)
                .build();
    }

    /**
     * Maneja errores de validación de objetos request en WebFlux (@RequestBody con @Valid).
     */
    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CustomErrorResponse handleWebExchangeBindException(WebExchangeBindException ex) {
        log.error("Error de validación WebFlux: {}", ex.getMessage(), ex);
        
        List<ObjectError> errors = ex.getAllErrors();
        List<String> notNullFields = new ArrayList<>();
        List<String> formatErrors = new ArrayList<>();
        
        for (ObjectError error : errors) {
            if (error instanceof FieldError fieldError) {
                String code = fieldError.getCode();
                if (Objects.equals("NotBlank", code) || 
                    Objects.equals("NotNull", code) || 
                    Objects.equals("NotEmpty", code)) {
                    notNullFields.add(fieldError.getField());
                } else {
                    formatErrors.add(String.format("%s: %s", 
                        fieldError.getField(), 
                        fieldError.getDefaultMessage()));
                }
            }
        }
        
        String mensaje;
        String codigo;
        
        if (!notNullFields.isEmpty()) {
            mensaje = "Debe asignar valor(es) para los siguientes campos: " + 
                     String.join(", ", notNullFields) + ".";
            codigo = BusinessErrorCodes.REQUIRED_FIELD_MISSING.getCode();
        } else {
            mensaje = "Errores de formato en los campos: " + String.join(", ", formatErrors) + ".";
            codigo = BusinessErrorCodes.INVALID_REQUEST_FORMAT.getCode();
        }
        
        CustomErrorResponse.ErrorDetail errorDetail = CustomErrorResponse.ErrorDetail.builder()
                .tipo("FUNCIONAL")
                .codigo(codigo)
                .mensaje(mensaje)
                .build();
        
        return CustomErrorResponse.builder()
                .error(errorDetail)
                .build();
    }
    
    /**
     * Maneja errores de decodificación en WebFlux.
     */
    @ExceptionHandler(DecodingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CustomErrorResponse handleDecodingException(DecodingException ex) {
        log.error("Error de decodificación: {}", ex.getMessage(), ex);
        
        String mensaje = "Error al procesar la solicitud: formato inválido.";
        
        Throwable cause = ex.getCause();
        while (cause != null) {
            String causeMessage = cause.getMessage();
            if (causeMessage != null) {
                if (causeMessage.contains("Cannot deserialize value of type")) {
                    if (causeMessage.contains("field \"")) {
                        int fieldStart = causeMessage.indexOf("field \"") + 7;
                        int fieldEnd = causeMessage.indexOf("\"", fieldStart);
                        if (fieldStart > 6 && fieldEnd > fieldStart) {
                            String fieldName = causeMessage.substring(fieldStart, fieldEnd);
                            mensaje = String.format("El campo '%s' tiene un formato inválido. Verifique el tipo de dato.", fieldName);
                            break;
                        }
                    }
                    if (causeMessage.contains("from String \"")) {
                        int valueStart = causeMessage.indexOf("from String \"") + 13;
                        int valueEnd = causeMessage.indexOf("\"", valueStart);
                        if (valueStart > 12 && valueEnd > valueStart) {
                            String invalidValue = causeMessage.substring(valueStart, valueEnd);
                            mensaje = String.format("El valor '%s' no es válido. Verifique el tipo de dato esperado.", invalidValue);
                            break;
                        }
                    }
                    mensaje = "Uno o más campos tienen un formato de dato inválido.";
                    break;
                }
                if (causeMessage.contains("For input string:")) {
                    int startIdx = causeMessage.indexOf("For input string: \"") + 19;
                    int endIdx = causeMessage.indexOf("\"", startIdx);
                    if (startIdx > 18 && endIdx > startIdx) {
                        String invalidValue = causeMessage.substring(startIdx, endIdx);
                        mensaje = String.format("El valor '%s' no es válido. Se esperaba un número.", invalidValue);
                        break;
                    }
                }
            }
            cause = cause.getCause();
        }
        
        CustomErrorResponse.ErrorDetail errorDetail = CustomErrorResponse.ErrorDetail.builder()
                .tipo("FUNCIONAL")
                .codigo(BusinessErrorCodes.INVALID_REQUEST_FORMAT.getCode())
                .mensaje(mensaje)
                .build();
        
        return CustomErrorResponse.builder()
                .error(errorDetail)
                .build();
    }

    /**
     * Maneja excepciones de negocio personalizadas.
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<CustomErrorResponse> handleBusinessException(BusinessException ex) {
        log.error("Error de negocio: {}", ex.getMessage(), ex);
        
        CustomErrorResponse.ErrorDetail errorDetail = CustomErrorResponse.ErrorDetail.builder()
                .tipo(ex.getType())
                .codigo(ex.getCode())
                .mensaje(ex.getMessage())
                .build();
        
        CustomErrorResponse errorResponse = CustomErrorResponse.builder()
                .error(errorDetail)
                .build();
        
        return new ResponseEntity<>(errorResponse, ex.getHttpStatus());
    }

    /**
     * Maneja errores de base de datos SQL y DataAccessException.
     */
    @ExceptionHandler({SQLException.class, DataAccessException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CustomErrorResponse handleDatabaseException(Exception ex) {
        log.error("Error de base de datos: {}", ex.getMessage(), ex);
        
        CustomErrorResponse.ErrorDetail errorDetail = CustomErrorResponse.ErrorDetail.builder()
                .tipo("SISTEMA")
                .codigo(BusinessErrorCodes.DATABASE_ERROR.getCode())
                .mensaje(BusinessErrorCodes.DATABASE_ERROR.getDescription())
                .build();
        
        return CustomErrorResponse.builder()
                .error(errorDetail)
                .build();
    }

    /**
     * Maneja RuntimeException genéricas no capturadas por otros handlers más específicos.
     * Este handler debe estar entre los últimos para no capturar excepciones que deberían
     * ser manejadas por otros handlers más específicos.
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CustomErrorResponse handleRuntimeException(RuntimeException ex) {
        log.error("Error de runtime no controlado: {}", ex.getMessage(), ex);
        
        String mensaje = ex.getMessage() != null ? ex.getMessage() : "Error interno del servidor";
        
        CustomErrorResponse.ErrorDetail errorDetail = CustomErrorResponse.ErrorDetail.builder()
                .tipo("SISTEMA")
                .codigo(BusinessErrorCodes.GENERIC_ERROR.getCode())
                .mensaje(mensaje)
                .build();
        
        return CustomErrorResponse.builder()
                .error(errorDetail)
                .build();
    }

    /**
     * Maneja todas las demás excepciones no controladas.
     * Este es el handler de último recurso.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CustomErrorResponse handleGenericException(Exception ex) {
        log.error("Error genérico: {}", ex.getMessage(), ex);
        
        CustomErrorResponse.ErrorDetail errorDetail = CustomErrorResponse.ErrorDetail.builder()
                .tipo("SISTEMA")
                .codigo(BusinessErrorCodes.GENERIC_ERROR.getCode())
                .mensaje(BusinessErrorCodes.GENERIC_ERROR.getDescription())
                .build();
        
        return CustomErrorResponse.builder()
                .error(errorDetail)
                .build();
    }
}
