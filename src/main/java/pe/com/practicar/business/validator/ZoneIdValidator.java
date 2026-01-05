package pe.com.practicar.business.validator;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import pe.com.practicar.business.exception.BusinessErrorCodes;
import pe.com.practicar.business.exception.BusinessException;
import pe.com.practicar.util.Constants;
import reactor.core.publisher.Mono;

@UtilityClass
public class ZoneIdValidator {

    private static final int MIN_ZONE_ID = 1;

    /**
     * Valida que el ID de zona sea válido.
     *
     * @param zoneId ID de la zona
     * @return Mono Boolean si es válido
     */
    public static Mono<Boolean> validate(Integer zoneId) {
        if (zoneId == null) {
            String message = Constants.RESPONSE_MESSAGE_ERROR_FORMAT_PARAMS
                    + "codigoZona es requerido.";
            return Mono.error(BusinessException.createException(
                    HttpStatus.BAD_REQUEST,
                    BusinessErrorCodes.REQUIRED_FIELD_MISSING));
        }

        if (zoneId < MIN_ZONE_ID) {
            String message = Constants.RESPONSE_MESSAGE_ERROR_FORMAT_PARAMS
                    + "codigoZona debe ser mayor o igual a " + MIN_ZONE_ID + ".";
            return Mono.error(BusinessException.createException(
                    HttpStatus.BAD_REQUEST,
                    BusinessErrorCodes.REQUIRED_FIELD_MISSING));
        }

        return Mono.just(true);
    }
}
