package pe.com.practicar.business.validator;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import pe.com.practicar.business.exception.BusinessErrorCodes;
import pe.com.practicar.business.exception.BusinessException;
import pe.com.practicar.expose.schema.ZoneDatosUpdateRequest;
import pe.com.practicar.util.Constants;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class ZoneUpdateRequestValidator {

    private static final String FORMAT_MESSAGE = Constants.RESPONSE_MESSAGE_ERROR_FORMAT_PARAMS;

    /**
     * Valida el request de actualización de zona.
     *
     * @param request Request de actualización
     * @return Mono Boolean si es válido
     */
    public static Mono<Boolean> validate(ZoneDatosUpdateRequest request) {
        return validateFieldFormats(request)
                .flatMap(errors -> {
                    if (!errors.isEmpty()) {
                        String message = FORMAT_MESSAGE + String.join(", ", errors);
                        return Mono.error(BusinessException.createException(
                                HttpStatus.BAD_REQUEST,
                                BusinessErrorCodes.REQUIRED_FIELD_MISSING,
                                List.of(message)));
                    }
                    return Mono.just(true);
                });
    }

    private static Mono<List<String>> validateFieldFormats(ZoneDatosUpdateRequest request) {
        return Mono.fromCallable(() -> {
            List<String> errors = new ArrayList<>();

            // Validar nombre si está presente
            if (request.getNombre() != null && !request.getNombre().isEmpty()) {
                if (!request.getNombre().matches(Constants.REGEX_ALPHANUMERIC_WITH_SPACES)) {
                    errors.add("nombre no debe contener caracteres especiales");
                }
                if (request.getNombre().length() > 200) {
                    errors.add("nombre no debe exceder 200 caracteres");
                }
            }

            // Validar coordenadas si están presentes
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

            // Validar descripción si está presente
            if (request.getDescripcion() != null && !request.getDescripcion().isEmpty()) {
                if (request.getDescripcion().length() > 500) {
                    errors.add("descripcion no debe exceder 500 caracteres");
                }
            }

            // Validar usuario actualización
            if (request.getUsuarioActualizacion() != null && !request.getUsuarioActualizacion().isEmpty()) {
                if (!request.getUsuarioActualizacion().matches(Constants.REGEX_ALPHANUMERIC_WITH_SPACES)) {
                    errors.add("usuarioActualizacion no debe contener caracteres especiales");
                }
            }

            return errors;
        }).subscribeOn(Schedulers.boundedElastic());
    }
}
