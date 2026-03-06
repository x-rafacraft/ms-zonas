package pe.com.practicar.business.validator;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import pe.com.practicar.business.exception.BusinessErrorCodes;
import pe.com.practicar.business.exception.BusinessException;
import pe.com.practicar.business.model.RiskLevel;
import pe.com.practicar.util.Constants;
import reactor.core.publisher.Mono;

import java.util.List;

@UtilityClass
public class PaginationValidator {

    private static final int MIN_PAGE = 1;
    private static final int MAX_PAGE = 1000;
    private static final int MIN_PAGE_SIZE = 1;
    private static final int MAX_PAGE_SIZE = 1000;

    /**
     * Valida los parámetros básicos de paginación.
     *
     * @param currentPage Página actual
     * @param pageSize Tamaño de página
     * @return Mono con true si es válido
     */
    public static Mono<Boolean> validatePagination(Integer currentPage, Integer pageSize) {
        if (currentPage == null || pageSize == null) {
            String message = Constants.RESPONSE_MESSAGE_ERROR_FORMAT_PARAMS 
                    + "Los parámetros paginaActual y tamanioPagina son obligatorios.";
            return Mono.error(BusinessException.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .code(BusinessErrorCodes.INVALID_PAGINATION.getCode())
                    .type(BusinessErrorCodes.INVALID_PAGINATION.getTitle())
                    .message(message)
                    .build());
        }

        if (currentPage < MIN_PAGE || currentPage > MAX_PAGE) {
            String message = Constants.RESPONSE_MESSAGE_ERROR_FORMAT_PARAMS 
                    + "paginaActual debe estar entre " + MIN_PAGE + " y " + MAX_PAGE + ".";
            return Mono.error(BusinessException.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .code(BusinessErrorCodes.INVALID_PAGINATION.getCode())
                    .type(BusinessErrorCodes.INVALID_PAGINATION.getTitle())
                    .message(message)
                    .build());
        }

        if (pageSize < MIN_PAGE_SIZE || pageSize > MAX_PAGE_SIZE) {
            String message = Constants.RESPONSE_MESSAGE_ERROR_FORMAT_PARAMS 
                    + "tamanioPagina debe estar entre " + MIN_PAGE_SIZE + " y " + MAX_PAGE_SIZE + ".";
            return Mono.error(BusinessException.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .code(BusinessErrorCodes.INVALID_PAGINATION.getCode())
                    .type(BusinessErrorCodes.INVALID_PAGINATION.getTitle())
                    .message(message)
                    .build());
        }

        return Mono.just(true);
    }

    /**
     * Valida los parámetros de paginación con filtros extendidos (ciudad, rango de riesgo).
     *
     * @param currentPage Página actual
     * @param pageSize Tamaño de página
     * @param province Provincia (opcional)
     * @param district Distrito (opcional)
     * @param securityLevel Nivel de seguridad exacto (opcional)
     * @param minRisk Nivel de riesgo mínimo como enum (opcional)
     * @param maxRisk Nivel de riesgo máximo como enum (opcional)
     * @return Mono con true si es válido
     */
    public static Mono<Boolean> validatePaginationWithFilters(Integer currentPage, Integer pageSize,
                                                               String province, String district,
                                                               Integer securityLevel,
                                                               RiskLevel minRisk, RiskLevel maxRisk) {
        return validatePagination(currentPage, pageSize)
                .flatMap(valid -> validateStringFilter(province, "provincia"))
                .flatMap(valid -> validateStringFilter(district, "distrito"))
                .flatMap(valid -> validateRiskLevel(securityLevel))
                .flatMap(valid -> validateRiskRange(minRisk, maxRisk));
    }

    /**
     * Sobrecarga sin rango de riesgo para compatibilidad.
     */
    public static Mono<Boolean> validatePaginationWithFilters(Integer currentPage, Integer pageSize,
                                                               String province, String district, Integer securityLevel) {
        return validatePaginationWithFilters(currentPage, pageSize, province, district, securityLevel, null, null);
    }

    /**
     * Valida que minRisk no sea mayor que maxRisk.
     *
     * @param minRisk Nivel de riesgo mínimo
     * @param maxRisk Nivel de riesgo máximo
     * @return Mono con true si el rango es válido
     */
    public static Mono<Boolean> validateRiskRange(RiskLevel minRisk, RiskLevel maxRisk) {
        if (minRisk != null && maxRisk != null && minRisk.ordinal() > maxRisk.ordinal()) {
            return Mono.error(BusinessException.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .code(BusinessErrorCodes.INVALID_RISK_RANGE.getCode())
                    .type(BusinessErrorCodes.INVALID_RISK_RANGE.getTitle())
                    .message("El rango de riesgo especificado no es válido")
                    .details(List.of("minRisk=" + minRisk.name() + ", maxRisk=" + maxRisk.name()))
                    .build());
        }
        return Mono.just(true);
    }

    private static Mono<Boolean> validateStringFilter(String filter, String paramName) {
        if (filter != null && !filter.isEmpty() && !filter.matches(Constants.REGEX_ALPHANUMERIC_WITH_SPACES)) {
            String message = Constants.RESPONSE_MESSAGE_ERROR_FORMAT_PARAMS
                    + paramName + " no debe contener caracteres especiales.";
            return Mono.error(BusinessException.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .code(BusinessErrorCodes.INVALID_PAGINATION.getCode())
                    .type(BusinessErrorCodes.INVALID_PAGINATION.getTitle())
                    .message(message)
                    .build());
        }
        return Mono.just(true);
    }

    private static Mono<Boolean> validateRiskLevel(Integer securityLevel) {
        if (securityLevel != null && (securityLevel < 1 || securityLevel > 10)) {
            String message = Constants.RESPONSE_MESSAGE_ERROR_FORMAT_PARAMS
                    + "nivelSeguridad debe estar entre 1 y 10.";
            return Mono.error(BusinessException.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .code(BusinessErrorCodes.INVALID_RISK_LEVEL.getCode())
                    .type(BusinessErrorCodes.INVALID_RISK_LEVEL.getTitle())
                    .message(message)
                    .build());
        }
        return Mono.just(true);
    }
}
