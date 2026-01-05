package pe.com.practicar.business.validator;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import pe.com.practicar.business.exception.BusinessErrorCodes;
import pe.com.practicar.business.exception.BusinessException;
import pe.com.practicar.expose.schema.ZoneDatosCreateRequest;
import pe.com.practicar.util.Constants;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class ZoneCreateRequestValidator {

    private static final String FORMAT_MESSAGE = Constants.RESPONSE_MESSAGE_ERROR_FORMAT_PARAMS;

    /**
     * Valida el request de creación de zona.
     *
     * @param request Request de creación
     * @return Mono Boolean si es válido
     */
    public static Mono<Boolean> validate(ZoneDatosCreateRequest request) {
        return validateRequiredFields(request)
                .flatMap(errors -> {
                    if (!errors.isEmpty()) {
                        String message = FORMAT_MESSAGE + String.join(", ", errors);
                        return Mono.error(BusinessException.createException(
                                HttpStatus.BAD_REQUEST,
                                BusinessErrorCodes.REQUIRED_FIELD_MISSING,
                                errors));
                    }
                    return validateFieldFormats(request);
                })
                .flatMap(errors -> {
                    if (!errors.isEmpty()) {
                        String message = FORMAT_MESSAGE + String.join(", ", errors);
                        return Mono.error(BusinessException.createException(
                                HttpStatus.BAD_REQUEST,
                                BusinessErrorCodes.REQUIRED_FIELD_MISSING,
                                errors));
                    }
                    return Mono.just(true);
                });
    }

    private static Mono<List<String>> validateRequiredFields(ZoneDatosCreateRequest request) {
        return Mono.fromCallable(() -> {
            List<String> errors = new ArrayList<>();

            if (request.getCodzona() == null) {
                errors.add("codzona es requerido");
            }

            if (request.getNombre() == null || request.getNombre().isEmpty()) {
                errors.add("nombre es requerido");
            }

            if (request.getDistrito() == null || request.getDistrito().isEmpty()) {
                errors.add("distrito es requerido");
            }

            if (request.getProvincia() == null || request.getProvincia().isEmpty()) {
                errors.add("provincia es requerido");
            }

            if (request.getRegion() == null || request.getRegion().isEmpty()) {
                errors.add("region es requerido");
            }

            if (request.getPais() == null || request.getPais().isEmpty()) {
                errors.add("pais es requerido");
            }

            if (request.getLatitud() == null) {
                errors.add("latitud es requerido");
            }

            if (request.getLongitud() == null) {
                errors.add("longitud es requerido");
            }

            if (request.getNivelSeguridad() == null) {
                errors.add("nivelSeguridad es requerido");
            }

            if (request.getUsuarioCreacion() == null || request.getUsuarioCreacion().isEmpty()) {
                errors.add("usuarioCreacion es requerido");
            }

            return errors;
        }).subscribeOn(Schedulers.boundedElastic());
    }

    private static Mono<List<String>> validateFieldFormats(ZoneDatosCreateRequest request) {
        return Mono.fromCallable(() -> {
            List<String> errors = new ArrayList<>();

            // Validar código de zona
            if (request.getCodzona() != null && request.getCodzona() < 1) {
                errors.add("codzona debe ser mayor o igual a 1");
            }

            // Validar nombre
            if (request.getNombre() != null && !request.getNombre().isEmpty()) {
                if (!request.getNombre().matches(Constants.REGEX_ALPHANUMERIC_WITH_SPACES)) {
                    errors.add("nombre no debe contener caracteres especiales");
                }
                if (request.getNombre().length() > 200) {
                    errors.add("nombre no debe exceder 200 caracteres");
                }
            }

            // Validar coordenadas
            if (request.getLatitud() != null) {
                if (request.getLatitud() < -90.0 || request.getLatitud() > 90.0) {
                    errors.add("latitud debe estar entre -90.0 y 90.0");
                }
            }

            if (request.getLongitud() != null) {
                if (request.getLongitud() < -180.0 || request.getLongitud() > 180.0) {
                    errors.add("longitud debe estar entre -180.0 y 180.0");
                }
            }

            // Validar nivel de seguridad
            if (request.getNivelSeguridad() != null) {
                if (request.getNivelSeguridad() < 1 || request.getNivelSeguridad() > 10) {
                    errors.add("nivelSeguridad debe estar entre 1 y 10");
                }
            }

            // Validar descripción
            if (request.getDescripcion() != null && !request.getDescripcion().isEmpty()) {
                if (request.getDescripcion().length() > 500) {
                    errors.add("descripcion no debe exceder 500 caracteres");
                }
            }

            // Validar usuario creación
            if (request.getUsuarioCreacion() != null && !request.getUsuarioCreacion().isEmpty()) {
                if (!request.getUsuarioCreacion().matches(Constants.REGEX_ALPHANUMERIC_WITH_SPACES)) {
                    errors.add("usuarioCreacion no debe contener caracteres especiales");
                }
            }

            // Validar campos de ubicación
            if (request.getDistrito() != null && !request.getDistrito().isEmpty()) {
                if (!request.getDistrito().matches(Constants.REGEX_ALPHANUMERIC_WITH_SPACES)) {
                    errors.add("distrito no debe contener caracteres especiales");
                }
            }

            if (request.getProvincia() != null && !request.getProvincia().isEmpty()) {
                if (!request.getProvincia().matches(Constants.REGEX_ALPHANUMERIC_WITH_SPACES)) {
                    errors.add("provincia no debe contener caracteres especiales");
                }
            }

            if (request.getRegion() != null && !request.getRegion().isEmpty()) {
                if (!request.getRegion().matches(Constants.REGEX_ALPHANUMERIC_WITH_SPACES)) {
                    errors.add("region no debe contener caracteres especiales");
                }
            }

            if (request.getPais() != null && !request.getPais().isEmpty()) {
                if (!request.getPais().matches(Constants.REGEX_ALPHANUMERIC_WITH_SPACES)) {
                    errors.add("pais no debe contener caracteres especiales");
                }
            }

            return errors;
        }).subscribeOn(Schedulers.boundedElastic());
    }
}
