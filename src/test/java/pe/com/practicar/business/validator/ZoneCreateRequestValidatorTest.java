package pe.com.practicar.business.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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

    @ParameterizedTest(name = "{0}")
    @CsvSource({
        "Nombre con caracteres especiales, Zona@#$%, -12.1191, -77.0292, 5",
        "Latitud fuera de rango, Zona Centro Lima, -91.0, -77.0292, 5",
        "Nivel seguridad menor a 1, Zona Centro Lima, -12.1191, -77.0292, 0",
        "Nivel seguridad mayor a 10, Zona Centro Lima, -12.1191, -77.0292, 11"
    })
    void validate_ConDatosInvalidos_DeberiaLanzarExcepcion(String escenario, String nombre, double latitud, double longitud, int nivelSeguridad) {
        ZoneDatosCreateRequest request = new ZoneDatosCreateRequest();
        request.setCodzona(1);
        request.setNombre(nombre);
        request.setLatitud(latitud);
        request.setLongitud(longitud);
        request.setNivelSeguridad(nivelSeguridad);
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
    void validate_ConCodigoMenorAUno_DeberiaLanzarExcepcion() {
        ZoneDatosCreateRequest request = new ZoneDatosCreateRequest();
        request.setCodzona(0);
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
    void validate_ConDescripcionMuyLarga_DeberiaLanzarExcepcion() {
        ZoneDatosCreateRequest request = new ZoneDatosCreateRequest();
        request.setCodzona(1);
        request.setNombre("Zona Centro Lima");
        request.setLatitud(-12.1191);
        request.setLongitud(-77.0292);
        request.setNivelSeguridad(5);
        request.setDescripcion("A".repeat(501));
        request.setUsuarioCreacion("admin");

        StepVerifier.create(ZoneCreateRequestValidator.validate(request))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void validate_ConUsuarioCreacionConCaracteresEspeciales_DeberiaLanzarExcepcion() {
        ZoneDatosCreateRequest request = new ZoneDatosCreateRequest();
        request.setCodzona(1);
        request.setNombre("Zona Centro Lima");
        request.setLatitud(-12.1191);
        request.setLongitud(-77.0292);
        request.setNivelSeguridad(5);
        request.setUsuarioCreacion("admin@#$");

        StepVerifier.create(ZoneCreateRequestValidator.validate(request))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void validate_ConDistritoConCaracteresEspeciales_DeberiaLanzarExcepcion() {
        ZoneDatosCreateRequest request = new ZoneDatosCreateRequest();
        request.setCodzona(1);
        request.setNombre("Zona Centro Lima");
        request.setDistrito("Miraflores@#$");
        request.setLatitud(-12.1191);
        request.setLongitud(-77.0292);
        request.setNivelSeguridad(5);
        request.setUsuarioCreacion("admin");

        StepVerifier.create(ZoneCreateRequestValidator.validate(request))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void validate_ConProvinciaConCaracteresEspeciales_DeberiaLanzarExcepcion() {
        ZoneDatosCreateRequest request = new ZoneDatosCreateRequest();
        request.setCodzona(1);
        request.setNombre("Zona Centro Lima");
        request.setProvincia("Lima@#$");
        request.setLatitud(-12.1191);
        request.setLongitud(-77.0292);
        request.setNivelSeguridad(5);
        request.setUsuarioCreacion("admin");

        StepVerifier.create(ZoneCreateRequestValidator.validate(request))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void validate_ConRegionConCaracteresEspeciales_DeberiaLanzarExcepcion() {
        ZoneDatosCreateRequest request = new ZoneDatosCreateRequest();
        request.setCodzona(1);
        request.setNombre("Zona Centro Lima");
        request.setRegion("Lima@#$");
        request.setLatitud(-12.1191);
        request.setLongitud(-77.0292);
        request.setNivelSeguridad(5);
        request.setUsuarioCreacion("admin");

        StepVerifier.create(ZoneCreateRequestValidator.validate(request))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void validate_ConPaisConCaracteresEspeciales_DeberiaLanzarExcepcion() {
        ZoneDatosCreateRequest request = new ZoneDatosCreateRequest();
        request.setCodzona(1);
        request.setNombre("Zona Centro Lima");
        request.setPais("Peru@#$");
        request.setLatitud(-12.1191);
        request.setLongitud(-77.0292);
        request.setNivelSeguridad(5);
        request.setUsuarioCreacion("admin");

        StepVerifier.create(ZoneCreateRequestValidator.validate(request))
                .expectError(BusinessException.class)
                .verify();
    }
}
