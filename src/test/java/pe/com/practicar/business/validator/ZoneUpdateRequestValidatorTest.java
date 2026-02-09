package pe.com.practicar.business.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pe.com.practicar.business.exception.BusinessException;
import pe.com.practicar.expose.schema.ZoneDatosUpdateRequest;
import reactor.test.StepVerifier;

import java.util.stream.Stream;

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
    void validate_ConDescripcionValida_DeberiaRetornarTrue() {
        ZoneDatosUpdateRequest request = new ZoneDatosUpdateRequest();
        request.setDescripcion("Descripción de prueba");

        StepVerifier.create(ZoneUpdateRequestValidator.validate(request))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void validate_ConUsuarioActualizacionValido_DeberiaRetornarTrue() {
        ZoneDatosUpdateRequest request = new ZoneDatosUpdateRequest();
        request.setUsuarioActualizacion("admin");

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
    void validate_ConDescripcionMuyLarga_DeberiaLanzarExcepcion() {
        ZoneDatosUpdateRequest request = new ZoneDatosUpdateRequest();
        request.setDescripcion("A".repeat(501));

        StepVerifier.create(ZoneUpdateRequestValidator.validate(request))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void validate_ConUsuarioActualizacionConCaracteresEspeciales_DeberiaLanzarExcepcion() {
        ZoneDatosUpdateRequest request = new ZoneDatosUpdateRequest();
        request.setUsuarioActualizacion("admin@#$");

        StepVerifier.create(ZoneUpdateRequestValidator.validate(request))
                .expectError(BusinessException.class)
                .verify();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideInvalidCoordinatesAndSecurityLevels")
    void validate_ConDatosInvalidos_DeberiaLanzarExcepcion(String testName, Double latitud, Double longitud, Integer nivelSeguridad) {
        ZoneDatosUpdateRequest request = new ZoneDatosUpdateRequest();
        request.setLatitud(latitud);
        request.setLongitud(longitud);
        request.setNivelSeguridad(nivelSeguridad);

        StepVerifier.create(ZoneUpdateRequestValidator.validate(request))
                .expectError(BusinessException.class)
                .verify();
    }

    private static Stream<Arguments> provideInvalidCoordinatesAndSecurityLevels() {
        return Stream.of(
                Arguments.of("Latitud menor a -90", -91.0, null, null),
                Arguments.of("Latitud mayor a 90", 91.0, null, null),
                Arguments.of("Longitud menor a -180", null, -181.0, null),
                Arguments.of("Longitud mayor a 180", null, 181.0, null),
                Arguments.of("Nivel seguridad menor a 1", null, null, 0),
                Arguments.of("Nivel seguridad mayor a 10", null, null, 11)
        );
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

    @Test
    void validate_ConNombreVacio_DeberiaRetornarTrue() {
        ZoneDatosUpdateRequest request = new ZoneDatosUpdateRequest();
        request.setNombre("");

        StepVerifier.create(ZoneUpdateRequestValidator.validate(request))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void validate_ConDescripcionVacia_DeberiaRetornarTrue() {
        ZoneDatosUpdateRequest request = new ZoneDatosUpdateRequest();
        request.setDescripcion("");

        StepVerifier.create(ZoneUpdateRequestValidator.validate(request))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void validate_ConUsuarioActualizacionVacio_DeberiaRetornarTrue() {
        ZoneDatosUpdateRequest request = new ZoneDatosUpdateRequest();
        request.setUsuarioActualizacion("");

        StepVerifier.create(ZoneUpdateRequestValidator.validate(request))
                .expectNext(true)
                .verifyComplete();
    }
}
