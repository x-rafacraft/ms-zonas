package pe.com.practicar.business.exception;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;

@Getter
public class BusinessException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String code;
    private final String type;
    private final List<String> details;

    @Builder
    public BusinessException(HttpStatus httpStatus, String code, String type, String message, List<String> details) {
        super(message);
        this.httpStatus = httpStatus != null ? httpStatus : HttpStatus.INTERNAL_SERVER_ERROR;
        this.code = code;
        this.type = type != null ? type : "BUSINESS_ERROR";
        this.details = details != null ? details : Collections.emptyList();
    }

    public static BusinessException createException(HttpStatus httpStatus, BusinessErrorCodes businessErrorCode) {
        return BusinessException.builder()
                .httpStatus(httpStatus)
                .code(businessErrorCode.getCode())
                .type(businessErrorCode.getTitle())
                .message(businessErrorCode.getDescription())
                .details(Collections.emptyList())
                .build();
    }

    public static BusinessException createException(HttpStatus httpStatus, BusinessErrorCodes businessErrorCode, List<String> details) {
        return BusinessException.builder()
                .httpStatus(httpStatus)
                .code(businessErrorCode.getCode())
                .type(businessErrorCode.getTitle())
                .message(businessErrorCode.getDescription())
                .details(details)
                .build();
    }
}
