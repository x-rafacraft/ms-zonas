package pe.com.practicar.business.validator;

import org.junit.jupiter.api.Test;
import pe.com.practicar.business.exception.BusinessException;
import pe.com.practicar.expose.schema.ZoneDatosCreateRequest;
import reactor.test.StepVerifier;

class ZoneCreateRequestValidatorTest {

    @Test
    void validate_ConRequestValido_DeberiaRetornarTrue() {
        ZoneDatosCreateRequest request = new ZoneDatosCreateRequest();
        request.setCodzona(1);
        request.setNombre("Zona Centro Lima");
        request.setDistrito("Miraflores");
        request.setProvincia("Lima");
        request.setRegion("Lima");
        request.setPais("Peru");
        request.setLatitud(-12.1191);
        request.setLongitud(-77.0292);
        request.setNivelSeguridad(5);
        request.setUsuarioCreacion("admin");

        StepVerifier.create(ZoneCreateRequestValidator.validate(request))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void validate_ConCodigoNull_DeberiaLanzarExcepcion() {
        ZoneDatosCreateRequest request = new ZoneDatosCreateRequest();
        request.setNombre("Zona Centro Lima");
        request.setLatitud(-12.1191);
        request.setLongitud(-77.0292);
        request.setNivelSeguridad(5);
        request.setUsuarioCreacion("admin");

        StepVerifier.create(ZoneCreateRequestValidator.validate(request))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void validate_ConNombreNull_DeberiaLanzarExcepcion() {
        ZoneDatosCreateRequest request = new ZoneDatosCreateRequest();
        request.setCodzona(1);
        request.setLatitud(-12.1191);
        request.setLongitud(-77.0292);
        request.setNivelSeguridad(5);
        request.setUsuarioCreacion("admin");

        StepVerifier.create(ZoneCreateRequestValidator.validate(request))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void validate_ConLatitudNull_DeberiaLanzarExcepcion() {
        ZoneDatosCreateRequest request = new ZoneDatosCreateRequest();
        request.setCodzona(1);
        request.setNombre("Zona Centro Lima");
        request.setLongitud(-77.0292);
        request.setNivelSeguridad(5);
        request.setUsuarioCreacion("admin");

        StepVerifier.create(ZoneCreateRequestValidator.validate(request))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void validate_ConLongitudNull_DeberiaLanzarExcepcion() {
        ZoneDatosCreateRequest request = new ZoneDatosCreateRequest();
        request.setCodzona(1);
        request.setNombre("Zona Centro Lima");
        request.setLatitud(-12.1191);
        request.setNivelSeguridad(5);
        request.setUsuarioCreacion("admin");

        StepVerifier.create(ZoneCreateRequestValidator.validate(request))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void validate_ConNivelSeguridadNull_DeberiaLanzarExcepcion() {
        ZoneDatosCreateRequest request = new ZoneDatosCreateRequest();
        request.setCodzona(1);
        request.setNombre("Zona Centro Lima");
        request.setLatitud(-12.1191);
        request.setLongitud(-77.0292);
        request.setUsuarioCreacion("admin");

        StepVerifier.create(ZoneCreateRequestValidator.validate(request))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void validate_ConNombreConCaracteresEspeciales_DeberiaLanzarExcepcion() {
        ZoneDatosCreateRequest request = new ZoneDatosCreateRequest();
        request.setCodzona(1);
        request.setNombre("Zona@#$%");
        request.setLatitud(-12.1191);
        request.setLongitud(-77.0292);
        request.setNivelSeguridad(5);
        request.setUsuarioCreacion("admin");

        StepVerifier.create(ZoneCreateRequestValidator.validate(request))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void validate_ConNombreMuyLargo_DeberiaLanzarExcepcion() {
        ZoneDatosCreateRequest request = new ZoneDatosCreateRequest();
        request.setCodzona(1);
        request.setNombre("A".repeat(201));
        request.setLatitud(-12.1191);
        request.setLongitud(-77.0292);
        request.setNivelSeguridad(5);
        request.setUsuarioCreacion("admin");

        StepVerifier.create(ZoneCreateRequestValidator.validate(request))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void validate_ConLatitudFueraDeRango_DeberiaLanzarExcepcion() {
        ZoneDatosCreateRequest request = new ZoneDatosCreateRequest();
        request.setCodzona(1);
        request.setNombre("Zona Centro Lima");
        request.setLatitud(-91.0);
        request.setLongitud(-77.0292);
        request.setNivelSeguridad(5);
        request.setUsuarioCreacion("admin");

        StepVerifier.create(ZoneCreateRequestValidator.validate(request))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void validate_ConLongitudFueraDeRango_DeberiaLanzarExcepcion() {
        ZoneDatosCreateRequest request = new ZoneDatosCreateRequest();
        request.setCodzona(1);
        request.setNombre("Zona Centro Lima");
        request.setLatitud(-12.1191);
        request.setLongitud(181.0);
        request.setNivelSeguridad(5);
        request.setUsuarioCreacion("admin");

        StepVerifier.create(ZoneCreateRequestValidator.validate(request))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void validate_ConNivelSeguridadMenorAUno_DeberiaLanzarExcepcion() {
        ZoneDatosCreateRequest request = new ZoneDatosCreateRequest();
        request.setCodzona(1);
        request.setNombre("Zona Centro Lima");
        request.setLatitud(-12.1191);
        request.setLongitud(-77.0292);
        request.setNivelSeguridad(0);
        request.setUsuarioCreacion("admin");

        StepVerifier.create(ZoneCreateRequestValidator.validate(request))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void validate_ConNivelSeguridadMayorADiez_DeberiaLanzarExcepcion() {
        ZoneDatosCreateRequest request = new ZoneDatosCreateRequest();
        request.setCodzona(1);
        request.setNombre("Zona Centro Lima");
        request.setLatitud(-12.1191);
        request.setLongitud(-77.0292);
        request.setNivelSeguridad(11);
        request.setUsuarioCreacion("admin");

        StepVerifier.create(ZoneCreateRequestValidator.validate(request))
                .expectError(BusinessException.class)
                .verify();
    }
}
