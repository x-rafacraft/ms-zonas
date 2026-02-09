package pe.com.practicar.business.validator;

import org.junit.jupiter.api.Test;
import pe.com.practicar.business.exception.BusinessException;
import reactor.test.StepVerifier;

class PaginationValidatorTest {

    @Test
    void validatePagination_ConParametrosValidos_DeberiaRetornarTrue() {
        StepVerifier.create(PaginationValidator.validatePagination(1, 10))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void validatePagination_ConPaginaMaxima_DeberiaRetornarTrue() {
        StepVerifier.create(PaginationValidator.validatePagination(1000, 10))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void validatePagination_ConTamanioMaximo_DeberiaRetornarTrue() {
        StepVerifier.create(PaginationValidator.validatePagination(1, 1000))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void validatePagination_ConPaginaNull_DeberiaLanzarExcepcion() {
        StepVerifier.create(PaginationValidator.validatePagination(null, 10))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void validatePagination_ConTamanioNull_DeberiaLanzarExcepcion() {
        StepVerifier.create(PaginationValidator.validatePagination(1, null))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void validatePagination_ConPaginaCero_DeberiaLanzarExcepcion() {
        StepVerifier.create(PaginationValidator.validatePagination(0, 10))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void validatePagination_ConPaginaNegativa_DeberiaLanzarExcepcion() {
        StepVerifier.create(PaginationValidator.validatePagination(-1, 10))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void validatePagination_ConPaginaMayorAMaximo_DeberiaLanzarExcepcion() {
        StepVerifier.create(PaginationValidator.validatePagination(1001, 10))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void validatePagination_ConTamanioCero_DeberiaLanzarExcepcion() {
        StepVerifier.create(PaginationValidator.validatePagination(1, 0))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void validatePagination_ConTamanioNegativo_DeberiaLanzarExcepcion() {
        StepVerifier.create(PaginationValidator.validatePagination(1, -1))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void validatePagination_ConTamanioMayorAMaximo_DeberiaLanzarExcepcion() {
        StepVerifier.create(PaginationValidator.validatePagination(1, 1001))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void validatePaginationWithFilters_ConNivelSeguridadValido_DeberiaRetornarTrue() {
        StepVerifier.create(PaginationValidator.validatePaginationWithFilters(1, 10, "Lima", "Miraflores", 5))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void validatePaginationWithFilters_ConNivelSeguridadMinimo_DeberiaRetornarTrue() {
        StepVerifier.create(PaginationValidator.validatePaginationWithFilters(1, 10, null, null, 1))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void validatePaginationWithFilters_ConNivelSeguridadMaximo_DeberiaRetornarTrue() {
        StepVerifier.create(PaginationValidator.validatePaginationWithFilters(1, 10, null, null, 10))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void validatePaginationWithFilters_ConNivelSeguridadCero_DeberiaLanzarExcepcion() {
        StepVerifier.create(PaginationValidator.validatePaginationWithFilters(1, 10, null, null, 0))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void validatePaginationWithFilters_ConNivelSeguridadMayorADiez_DeberiaLanzarExcepcion() {
        StepVerifier.create(PaginationValidator.validatePaginationWithFilters(1, 10, null, null, 11))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void validatePaginationWithFilters_ConNivelSeguridadNull_DeberiaRetornarTrue() {
        StepVerifier.create(PaginationValidator.validatePaginationWithFilters(1, 10, "Lima", null, null))
                .expectNext(true)
                .verifyComplete();
    }
}
