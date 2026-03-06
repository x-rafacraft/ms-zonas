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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
        List<Zones> result = repository.getZonesPaginated(1, 10);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals("Zona Test", result.get(0).getName());
    }

    // @Test
    void createZone_DeberiaRetornarZonaCreada() {
        // Given
        ZoneDatosCreateRequest request = new ZoneDatosCreateRequest();
        request.setCodzona(100);
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
        createdZone.setId(100);
        createdZone.setName("Nueva Zona");
        
        when(namedParameterJdbcTemplate.update(
                anyString(),
                any(MapSqlParameterSource.class)
        )).thenReturn(1);

        when(namedParameterJdbcTemplate.query(
                anyString(),
                any(MapSqlParameterSource.class),
                any(BeanPropertyRowMapper.class)
        )).thenReturn(Arrays.asList(createdZone));

        // When
        Zones result = repository.createZone(request);

        // Then
        assertNotNull(result);
        assertEquals(100, result.getId());
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
        Optional<Zones> result = repository.updateZone(1, request);

        // Then
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
        assertEquals(-12.0464, result.get().getLatitude());
    }

    @Test
    void updateZone_ZonaNoExiste_DeberiaRetornarEmpty() {
        // Given
        ZoneDatosUpdateRequest request = new ZoneDatosUpdateRequest();
        request.setLatitud(-12.0464);

        when(namedParameterJdbcTemplate.update(
                anyString(),
                any(MapSqlParameterSource.class)
        )).thenReturn(0);

        when(namedParameterJdbcTemplate.query(
                anyString(),
                any(MapSqlParameterSource.class),
                any(BeanPropertyRowMapper.class)
        )).thenReturn(Collections.emptyList());

        // When
        Optional<Zones> result = repository.updateZone(999, request);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void getZonesWithFilters_DeberiaRetornarListaFiltrada() {
        // Given
        Zones zone = new Zones();
        zone.setId(1);
        zone.setName("Zona Central Lima");
        zone.setProvince("Lima");
        zone.setDistrict("Miraflores");
        zone.setSecurityLevel(4);
        List<Zones> zones = Arrays.asList(zone);

        when(namedParameterJdbcTemplate.query(
                anyString(),
                any(MapSqlParameterSource.class),
                any(BeanPropertyRowMapper.class)
        )).thenReturn(zones);

        // When
        List<Zones> result = repository.getZonesWithFilters(1, 10, "Lima", "Miraflores", 4, null, null, null);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Lima", result.get(0).getProvince());
        assertEquals("Miraflores", result.get(0).getDistrict());
        assertEquals(4, result.get(0).getSecurityLevel());
    }
}
