package pe.com.practicar.repository.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.test.util.ReflectionTestUtils;
import pe.com.practicar.expose.schema.ZoneDatosCreateRequest;
import pe.com.practicar.expose.schema.ZoneDatosUpdateRequest;
import pe.com.practicar.repository.model.Zones;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ZonesJdbcRepositoryImplTest {

    @Mock
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private ZonesJdbcRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new ZonesJdbcRepositoryImpl(namedParameterJdbcTemplate);
        ReflectionTestUtils.setField(repository, "schema", "PRUEBA00");
    }

    @Test
    void getZonesPaginated_DeberiaRetornarListaDeZonas() {
        // Given
        Zones zone = new Zones();
        zone.setId(1);
        zone.setName("Zona Test");
        List<Zones> zones = Arrays.asList(zone);

        when(namedParameterJdbcTemplate.query(
                anyString(),
                any(MapSqlParameterSource.class),
                any(BeanPropertyRowMapper.class)
        )).thenReturn(zones);

        // When
        Mono<List<Zones>> result = repository.getZonesPaginated(1, 10);

        // Then
        StepVerifier.create(result)
                .expectNext(zones)
                .verifyComplete();
    }

    @Test
    void createZone_DeberiaRetornarZonaCreada() {
        // Given
        ZoneDatosCreateRequest request = new ZoneDatosCreateRequest();
        request.setNombre("Nueva Zona");
        request.setDistrito("Miraflores");
        request.setProvincia("Lima");
        request.setRegion("Lima");
        request.setPais("Perú");
        request.setLatitud(-12.1191);
        request.setLongitud(-77.0292);
        request.setNivelSeguridad(4);
        request.setUsuarioCreacion("admin");

        Zones createdZone = new Zones();
        createdZone.setId(1);
        createdZone.setName("Nueva Zona");
        
        when(namedParameterJdbcTemplate.update(
                anyString(),
                any(MapSqlParameterSource.class),
                any(GeneratedKeyHolder.class),
                any(String[].class)
        )).thenAnswer(invocation -> {
            GeneratedKeyHolder holder = invocation.getArgument(2);
            holder.getKeyList().add(java.util.Map.of("codzona", 1));
            return 1;
        });

        when(namedParameterJdbcTemplate.query(
                anyString(),
                any(MapSqlParameterSource.class),
                any(BeanPropertyRowMapper.class)
        )).thenReturn(Arrays.asList(createdZone));

        // When
        Mono<Zones> result = repository.createZone(request);

        // Then
        StepVerifier.create(result)
                .expectNext(createdZone)
                .verifyComplete();
    }

    @Test
    void updateZone_DeberiaRetornarZonaActualizada() {
        // Given
        ZoneDatosUpdateRequest request = new ZoneDatosUpdateRequest();
        request.setLatitud(-12.0464);
        request.setLongitud(-77.0428);
        request.setNivelSeguridad(5);

        Zones updatedZone = new Zones();
        updatedZone.setId(1);
        updatedZone.setLatitude(-12.0464);

        when(namedParameterJdbcTemplate.queryForObject(
                anyString(),
                any(MapSqlParameterSource.class),
                eq(Integer.class)
        )).thenReturn(1);

        when(namedParameterJdbcTemplate.update(
                anyString(),
                any(MapSqlParameterSource.class)
        )).thenReturn(1);

        when(namedParameterJdbcTemplate.query(
                anyString(),
                any(MapSqlParameterSource.class),
                any(BeanPropertyRowMapper.class)
        )).thenReturn(Arrays.asList(updatedZone));

        // When
        Mono<Zones> result = repository.updateZone(1, request);

        // Then
        StepVerifier.create(result)
                .expectNext(updatedZone)
                .verifyComplete();
    }

    @Test
    void updateZone_ZonaNoExiste_DeberiaLanzarExcepcion() {
        // Given
        ZoneDatosUpdateRequest request = new ZoneDatosUpdateRequest();
        request.setLatitud(-12.0464);

        when(namedParameterJdbcTemplate.queryForObject(
                anyString(),
                any(MapSqlParameterSource.class),
                eq(Integer.class)
        )).thenReturn(0);

        // When
        Mono<Zones> result = repository.updateZone(999, request);

        // Then
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }
}
