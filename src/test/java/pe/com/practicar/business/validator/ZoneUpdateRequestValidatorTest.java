package pe.com.practicar.business.validator;

import org.junit.jupiter.api.Test;
import pe.com.practicar.business.exception.BusinessException;
import pe.com.practicar.expose.schema.ZoneDatosUpdateRequest;
import reactor.test.StepVerifier;

class ZoneUpdateRequestValidatorTest {

    @Test
    void validate_ConRequestVacio_DeberiaRetornarTrue() {
        ZoneDatosUpdateRequest request = new ZoneDatosUpdateRequest();

        StepVerifier.create(ZoneUpdateRequestValidator.validate(request))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void validate_ConNombreValido_DeberiaRetornarTrue() {
        ZoneDatosUpdateRequest request = new ZoneDatosUpdateRequest();
        request.setNombre("Zona Actualizada");

        StepVerifier.create(ZoneUpdateRequestValidator.validate(request))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void validate_ConCoordenadasValidas_DeberiaRetornarTrue() {
        ZoneDatosUpdateRequest request = new ZoneDatosUpdateRequest();
        request.setLatitud(-12.0464);
        request.setLongitud(-77.0428);

        StepVerifier.create(ZoneUpdateRequestValidator.validate(request))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void validate_ConNivelSeguridadValido_DeberiaRetornarTrue() {
        ZoneDatosUpdateRequest request = new ZoneDatosUpdateRequest();
        request.setNivelSeguridad(8);

        StepVerifier.create(ZoneUpdateRequestValidator.validate(request))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void validate_ConNombreConCaracteresEspeciales_DeberiaLanzarExcepcion() {
        ZoneDatosUpdateRequest request = new ZoneDatosUpdateRequest();
        request.setNombre("Zona@#$%");

        StepVerifier.create(ZoneUpdateRequestValidator.validate(request))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void validate_ConNombreMuyLargo_DeberiaLanzarExcepcion() {
        ZoneDatosUpdateRequest request = new ZoneDatosUpdateRequest();
        request.setNombre("A".repeat(201));

        StepVerifier.create(ZoneUpdateRequestValidator.validate(request))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void validate_ConLatitudFueraDeRangoMinimo_DeberiaLanzarExcepcion() {
        ZoneDatosUpdateRequest request = new ZoneDatosUpdateRequest();
        request.setLatitud(-91.0);

        StepVerifier.create(ZoneUpdateRequestValidator.validate(request))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void validate_ConLatitudFueraDeRangoMaximo_DeberiaLanzarExcepcion() {
        ZoneDatosUpdateRequest request = new ZoneDatosUpdateRequest();
        request.setLatitud(91.0);

        StepVerifier.create(ZoneUpdateRequestValidator.validate(request))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void validate_ConLongitudFueraDeRangoMinimo_DeberiaLanzarExcepcion() {
        ZoneDatosUpdateRequest request = new ZoneDatosUpdateRequest();
        request.setLongitud(-181.0);

        StepVerifier.create(ZoneUpdateRequestValidator.validate(request))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void validate_ConLongitudFueraDeRangoMaximo_DeberiaLanzarExcepcion() {
        ZoneDatosUpdateRequest request = new ZoneDatosUpdateRequest();
        request.setLongitud(181.0);

        StepVerifier.create(ZoneUpdateRequestValidator.validate(request))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void validate_ConNivelSeguridadMenorAUno_DeberiaLanzarExcepcion() {
        ZoneDatosUpdateRequest request = new ZoneDatosUpdateRequest();
        request.setNivelSeguridad(0);

        StepVerifier.create(ZoneUpdateRequestValidator.validate(request))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void validate_ConNivelSeguridadMayorADiez_DeberiaLanzarExcepcion() {
        ZoneDatosUpdateRequest request = new ZoneDatosUpdateRequest();
        request.setNivelSeguridad(11);

        StepVerifier.create(ZoneUpdateRequestValidator.validate(request))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void validate_ConTodosCamposValidos_DeberiaRetornarTrue() {
        ZoneDatosUpdateRequest request = new ZoneDatosUpdateRequest();
        request.setNombre("Zona Actualizada");
        request.setLatitud(-12.0464);
        request.setLongitud(-77.0428);
        request.setNivelSeguridad(7);
        request.setDescripcion("Nueva descripcion");
        request.setActivo(true);
        request.setUsuarioActualizacion("admin");

        StepVerifier.create(ZoneUpdateRequestValidator.validate(request))
                .expectNext(true)
                .verifyComplete();
    }
}
