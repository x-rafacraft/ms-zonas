package pe.com.practicar.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pe.com.practicar.business.dto.CustomErrorResponse;
import pe.com.practicar.business.exception.BusinessException;

@RestControllerAdvice
@Slf4j
public class GenericExceptionHandler {

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
}
