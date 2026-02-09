package pe.com.practicar.business.validator;

import org.junit.jupiter.api.Test;
import pe.com.practicar.business.exception.BusinessException;
import reactor.test.StepVerifier;

class ZoneIdValidatorTest {

    @Test
    void validate_ConIdValido_DeberiaRetornarTrue() {
        StepVerifier.create(ZoneIdValidator.validate(1))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void validate_ConIdGrande_DeberiaRetornarTrue() {
        StepVerifier.create(ZoneIdValidator.validate(9999))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void validate_ConIdNull_DeberiaLanzarExcepcion() {
        StepVerifier.create(ZoneIdValidator.validate(null))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void validate_ConIdCero_DeberiaLanzarExcepcion() {
        StepVerifier.create(ZoneIdValidator.validate(0))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void validate_ConIdNegativo_DeberiaLanzarExcepcion() {
        StepVerifier.create(ZoneIdValidator.validate(-1))
                .expectError(BusinessException.class)
                .verify();
    }
}
