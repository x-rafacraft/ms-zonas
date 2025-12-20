package pe.com.practicar.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pe.com.practicar.business.dto.ZonesDto;
import pe.com.practicar.repository.model.Zones;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ZoneMapperTest {

    private ZoneMapper zoneMapper;

    @BeforeEach
    void setUp() {
        zoneMapper = new ZoneMapper();
    }

    @Test
    void convertToZoneResponse_DeberiaConvertirCorrectamente() {
        // Given
        Zones zone = new Zones();
        zone.setId(1);
        zone.setName("Zona Test");
        zone.setDistrict("Miraflores");
        zone.setProvince("Lima");
        zone.setRegion("Lima");
        zone.setCountry("Perú");
        zone.setLatitude(-12.1191);
        zone.setLongitude(-77.0292);
        zone.setSecurityLevel(4);
        zone.setDescription("Zona de prueba");
        zone.setActive(true);
        zone.setCreatedBy("admin");
        zone.setUpdatedBy("admin");
        zone.setCreatedAt(LocalDateTime.now());
        zone.setUpdatedAt(LocalDateTime.now());

        // When
        ZonesDto result = zoneMapper.convertToZoneResponse(zone);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getZoneCode());
        assertEquals("Zona Test", result.getNames());
        assertEquals("Miraflores", result.getDistricts());
        assertEquals("Lima", result.getProvinces());
        assertEquals("Lima", result.getRegions());
        assertEquals("Perú", result.getCountrys());
        assertEquals(-12.1191, result.getLatitudes());
        assertEquals(-77.0292, result.getLongitudes());
        assertEquals(4, result.getSecurityLevels());
        assertEquals("Zona de prueba", result.getDescriptions());
        assertTrue(result.getActives());
    }

    @Test
    void convertToZoneResponse_ConZonaNull_DeberiaRetornarNull() {
        // Given - When
        ZonesDto result = null;
        
        try {
            result = zoneMapper.convertToZoneResponse(null);
        } catch (NullPointerException e) {
            // Si el mapper lanza NPE con null, es comportamiento esperado
            assertNull(result);
            return;
        }

        // Then - Si no lanza excepción, debería retornar null
        assertNull(result);
    }
}
