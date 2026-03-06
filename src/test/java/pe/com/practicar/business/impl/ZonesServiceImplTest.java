package pe.com.practicar.business.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import pe.com.practicar.business.dto.ZonesDto;
import pe.com.practicar.business.dto.ZonesPaginatedDto;
import pe.com.practicar.business.exception.BusinessException;
import pe.com.practicar.business.model.RiskLevel;
import pe.com.practicar.mapper.ZoneMapper;
import pe.com.practicar.expose.schema.ZoneDatosCreateRequest;
import pe.com.practicar.expose.schema.ZoneDatosUpdateRequest;
import pe.com.practicar.repository.ZonesJdbcRepository;
import pe.com.practicar.repository.model.ZoneSummaryByLevel;
import pe.com.practicar.repository.model.Zones;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ZonesServiceImplTest {

    @Mock
    private ZonesJdbcRepository zonesJdbcRepository;

    @Mock
    private ZoneMapper zoneMapper;

    @InjectMocks
    private ZonesServiceImpl zonesService;

    @Test
    void zonesList_DeberiaRetornarListaDeTos() {
        // Given
        List<Zones> mockZones = Arrays.asList(new Zones(), new Zones());
        when(zonesJdbcRepository.getZonesPaginated(anyInt(), anyInt()))
                .thenReturn(mockZones);

        // When
        Mono<?> result = zonesService.zonesList(1, 10);

        // Then
        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();
    }

    // @Test
    void zonesList_ConPaginaInvalida_DeberiaManejarlo() {
        // Given
        when(zonesJdbcRepository.getZonesPaginated(anyInt(), anyInt()))
                .thenReturn(Collections.emptyList());

        // When
        Mono<?> result = zonesService.zonesList(0, 10);

        // Then
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void zonesListWithFilters_DeberiaRetornarListaFiltrada() {
        // Given
        List<Zones> mockZones = Arrays.asList(new Zones(), new Zones());
        when(zonesJdbcRepository.getZonesWithFilters(anyInt(), anyInt(),
                any(), any(), any(), any(), any(), any()))
                .thenReturn(mockZones);

        // When
        Mono<?> result = zonesService.zonesListWithFilters(1, 10, "Lima", "Miraflores", 4, null, null, null);

        // Then
        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void getZonesSummary_DeberiaRetornarResumen() {
        // Given
        ZoneSummaryByLevel summary1 = ZoneSummaryByLevel.builder()
                .securityLevel(1)
                .count(5L)
                .build();
        ZoneSummaryByLevel summary2 = ZoneSummaryByLevel.builder()
                .securityLevel(4)
                .count(10L)
                .build();
        
        when(zonesJdbcRepository.getZonesSummaryBySecurityLevel())
                .thenReturn(Arrays.asList(summary1, summary2));
        when(zonesJdbcRepository.getTotalZonesCount())
                .thenReturn(15L);

        // When
        Mono<?> result = zonesService.getZonesSummary();

        // Then
        StepVerifier.create(result)
                .expectNextMatches(summary -> {
                    var zoneSummary = (pe.com.practicar.business.dto.ZoneSummaryDto) summary;
                    return zoneSummary.getTotalZonas() == 15L && 
                           zoneSummary.getResumenPorNivel().size() == 2;
                })
                .verifyComplete();
    }

    @Test
    void getZonesSummary_ConErrorEnRepositorio_DeberiaLanzarExcepcion() {
        // Given
        when(zonesJdbcRepository.getZonesSummaryBySecurityLevel())
                .thenThrow(new RuntimeException("Error de BD"));
        when(zonesJdbcRepository.getTotalZonesCount())
                .thenReturn(0L);

        // When & Then
        StepVerifier.create(zonesService.getZonesSummary())
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void getZoneById_ConIdValido_DeberiaRetornarZona() {
        // Given
        Zones mockZone = createMockZone();
        ZonesDto mockDto = createMockDto();
        
        when(zonesJdbcRepository.getZoneById(1))
                .thenReturn(java.util.Optional.of(mockZone));
        when(zoneMapper.convertToZoneResponse(any(Zones.class)))
                .thenReturn(mockDto);

        // When & Then
        StepVerifier.create(zonesService.getZoneById(1))
                .expectNext(mockDto)
                .verifyComplete();
    }

    @Test
    void getZoneById_ConIdInexistente_DeberiaLanzarExcepcion() {
        // Given
        when(zonesJdbcRepository.getZoneById(999))
                .thenReturn(java.util.Optional.empty());

        // When & Then
        StepVerifier.create(zonesService.getZoneById(999))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void getZoneById_ConIdNull_DeberiaLanzarExcepcion() {
        // When & Then
        StepVerifier.create(zonesService.getZoneById(null))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void createZone_ConRequestValido_DeberiaCrearZona() {
        // Given
        ZoneDatosCreateRequest request = createMockCreateRequest();
        Zones mockZone = createMockZone();
        ZonesDto mockDto = createMockDto();
        
        when(zonesJdbcRepository.existsByCodzona(anyInt()))
                .thenReturn(false);
        when(zonesJdbcRepository.existsByNombre(anyString()))
                .thenReturn(false);
        when(zonesJdbcRepository.existsByCoordinates(anyDouble(), anyDouble()))
                .thenReturn(false);
        when(zonesJdbcRepository.createZone(any()))
                .thenReturn(mockZone);
        when(zoneMapper.convertToZoneResponse(any(Zones.class)))
                .thenReturn(mockDto);

        // When & Then
        StepVerifier.create(zonesService.createZone(request))
                .expectNext(mockDto)
                .verifyComplete();
    }

    @Test
    void createZone_ConCodigoExistente_DeberiaLanzarExcepcion() {
        // Given
        ZoneDatosCreateRequest request = createMockCreateRequest();
        
        when(zonesJdbcRepository.existsByCodzona(anyInt()))
                .thenReturn(true);

        // When & Then
        StepVerifier.create(zonesService.createZone(request))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void createZone_ConNombreExistente_DeberiaLanzarExcepcion() {
        // Given
        ZoneDatosCreateRequest request = createMockCreateRequest();
        
        when(zonesJdbcRepository.existsByCodzona(anyInt()))
                .thenReturn(false);
        when(zonesJdbcRepository.existsByNombre(anyString()))
                .thenReturn(true);

        // When & Then
        StepVerifier.create(zonesService.createZone(request))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void createZone_ConCoordenadasExistentes_DeberiaLanzarExcepcion() {
        // Given
        ZoneDatosCreateRequest request = createMockCreateRequest();
        
        when(zonesJdbcRepository.existsByCodzona(anyInt()))
                .thenReturn(false);
        when(zonesJdbcRepository.existsByNombre(anyString()))
                .thenReturn(false);
        when(zonesJdbcRepository.existsByCoordinates(anyDouble(), anyDouble()))
                .thenReturn(true);

        // When & Then
        StepVerifier.create(zonesService.createZone(request))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void updateZone_ConRequestValido_DeberiaActualizarZona() {
        // Given
        ZoneDatosUpdateRequest request = createMockUpdateRequest();
        Zones mockZone = createMockZone();
        ZonesDto mockDto = createMockDto();
        
        when(zonesJdbcRepository.existsByNombreExcludingId(anyString(), anyInt()))
                .thenReturn(false);
        when(zonesJdbcRepository.existsByCoordinatesExcludingId(anyDouble(), anyDouble(), anyInt()))
                .thenReturn(false);
        when(zonesJdbcRepository.updateZone(anyInt(), any()))
                .thenReturn(java.util.Optional.of(mockZone));
        when(zoneMapper.convertToZoneResponse(any(Zones.class)))
                .thenReturn(mockDto);

        // When & Then
        StepVerifier.create(zonesService.updateZone(1, request))
                .expectNext(mockDto)
                .verifyComplete();
    }

    @Test
    void updateZone_ConNombreDuplicado_DeberiaLanzarExcepcion() {
        // Given
        ZoneDatosUpdateRequest request = createMockUpdateRequest();
        
        when(zonesJdbcRepository.existsByNombreExcludingId(anyString(), anyInt()))
                .thenReturn(true);

        // When & Then
        StepVerifier.create(zonesService.updateZone(1, request))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void updateZone_ConCoordenadasDuplicadas_DeberiaLanzarExcepcion() {
        // Given
        ZoneDatosUpdateRequest request = createMockUpdateRequest();
        
        when(zonesJdbcRepository.existsByNombreExcludingId(anyString(), anyInt()))
                .thenReturn(false);
        when(zonesJdbcRepository.existsByCoordinatesExcludingId(anyDouble(), anyDouble(), anyInt()))
                .thenReturn(true);

        // When & Then
        StepVerifier.create(zonesService.updateZone(1, request))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void updateZone_ConZonaInexistente_DeberiaLanzarExcepcion() {
        // Given
        ZoneDatosUpdateRequest request = createMockUpdateRequest();
        
        when(zonesJdbcRepository.existsByNombreExcludingId(anyString(), anyInt()))
                .thenReturn(false);
        when(zonesJdbcRepository.existsByCoordinatesExcludingId(anyDouble(), anyDouble(), anyInt()))
                .thenReturn(false);
        when(zonesJdbcRepository.updateZone(anyInt(), any()))
                .thenReturn(java.util.Optional.empty());

        // When & Then
        StepVerifier.create(zonesService.updateZone(1, request))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void replaceZone_ConRequestValido_DeberiaReemplazarZona() {
        // Given
        ZoneDatosCreateRequest request = createMockCreateRequest();
        Zones mockZone = createMockZone();
        ZonesDto mockDto = createMockDto();
        
        when(zonesJdbcRepository.replaceZone(anyInt(), any()))
                .thenReturn(java.util.Optional.of(mockZone));
        when(zoneMapper.convertToZoneResponse(any(Zones.class)))
                .thenReturn(mockDto);

        // When & Then
        StepVerifier.create(zonesService.replaceZone(1, request))
                .expectNext(mockDto)
                .verifyComplete();
    }

    @Test
    void replaceZone_ConZonaInexistente_DeberiaLanzarExcepcion() {
        // Given
        ZoneDatosCreateRequest request = createMockCreateRequest();
        
        when(zonesJdbcRepository.replaceZone(anyInt(), any()))
                .thenReturn(java.util.Optional.empty());

        // When & Then
        StepVerifier.create(zonesService.replaceZone(1, request))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void deleteZone_ConIdValido_DeberiaEliminarZona() {
        // Given
        when(zonesJdbcRepository.deleteZone(1))
                .thenReturn(1);

        // When & Then
        StepVerifier.create(zonesService.deleteZone(1))
                .verifyComplete();
    }

    @Test
    void deleteZone_ConZonaInexistente_DeberiaLanzarExcepcion() {
        // Given
        when(zonesJdbcRepository.deleteZone(999))
                .thenReturn(0);

        // When & Then
        StepVerifier.create(zonesService.deleteZone(999))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void deleteZone_ConIdNull_DeberiaLanzarExcepcion() {
        // When & Then
        StepVerifier.create(zonesService.deleteZone(null))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void zonesListWithFilters_ConListaVacia_DeberiaRetornarPaginadoVacio() {
        // Given
        when(zonesJdbcRepository.getZonesWithFilters(anyInt(), anyInt(), any(), any(), any(), any(), any(), any()))
                .thenReturn(Collections.emptyList());

        // When & Then
        StepVerifier.create(zonesService.zonesListWithFilters(1, 10, null, null, null, null, null, null))
                .expectNextMatches(result -> {
                    var paginated = (pe.com.practicar.business.dto.ZonesPaginatedDto) result;
                    return paginated.getZones().isEmpty() &&
                           paginated.getCurrentPage() == 1 &&
                           paginated.getPageSize() == 10;
                })
                .verifyComplete();
    }

    @Test
    void zonesList_ConListaVacia_DeberiaRetornarPaginadoVacio() {
        // Given
        when(zonesJdbcRepository.getZonesPaginated(anyInt(), anyInt()))
                .thenReturn(Collections.emptyList());

        // When & Then
        StepVerifier.create(zonesService.zonesList(1, 10))
                .expectNextMatches(result -> {
                    var paginated = (pe.com.practicar.business.dto.ZonesPaginatedDto) result;
                    return paginated.getZones().isEmpty() && 
                           paginated.getCurrentPage() == 1 && 
                           paginated.getPageSize() == 10;
                })
                .verifyComplete();
    }

  
    // Caso 1: filtros válidos (ciudad=Lima, minRisk=MEDIUM, maxRisk=HIGH)
    @Test
    void zonesListWithFilters_ConCiudadYRangoValido_DeberiaRetornarResultados() {
        // Given
        List<Zones> mockZones = Arrays.asList(buildZone(1), buildZone(2), buildZone(3));
        when(zonesJdbcRepository.getZonesWithFilters(
                anyInt(), anyInt(),
                isNull(), isNull(), isNull(),
                eq("Lima"), eq(RiskLevel.MEDIUM), eq(RiskLevel.HIGH)))
                .thenReturn(mockZones);

        // When
        Mono<ZonesPaginatedDto> result = zonesService.zonesListWithFilters(
                1, 10, null, null, null, "Lima", RiskLevel.MEDIUM, RiskLevel.HIGH);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(paginated ->
                        paginated.getZones().size() == 3
                        && paginated.getCurrentPage() == 1
                        && paginated.getPageSize() == 10)
                .verifyComplete();
    }

    // Caso 2: sin filtros → devuelve todas las zonas
    @Test
    void zonesList_SinFiltros_DeberiaRetornarTodasLasZonas() {
        // Given
        List<Zones> todasLasZonas = Arrays.asList(
                buildZone(1), buildZone(2), buildZone(3), buildZone(4), buildZone(5));
        when(zonesJdbcRepository.getZonesPaginated(anyInt(), anyInt()))
                .thenReturn(todasLasZonas);

        // When
        Mono<ZonesPaginatedDto> result = zonesService.zonesList(1, 10);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(paginated ->
                        paginated.getZones().size() == 5
                        && paginated.getCurrentPage() == 1)
                .verifyComplete();
    }

    // Caso 3: rango inválido (minRisk=HIGH > maxRisk=LOW) → HTTP 400 INVALID_RISK_RANGE
    @Test
    void zonesListWithFilters_ConRangoInvalido_DeberiaRetornar400() {
        // When
        Mono<ZonesPaginatedDto> result = zonesService.zonesListWithFilters(
                1, 10, null, null, null, null, RiskLevel.HIGH, RiskLevel.LOW);

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(error ->
                        error instanceof BusinessException businessEx
                        && "018".equals(businessEx.getCode())
                        && "INVALID_RISK_RANGE".equals(businessEx.getType())
                        && businessEx.getHttpStatus().value() == 400
                        && businessEx.getDetails().stream()
                                .anyMatch(d -> d.contains("minRisk=HIGH") && d.contains("maxRisk=LOW")))
                .verify();
    }

    // Extra: rango igual MEDIUM=MEDIUM es válido (caso borde)
    @Test
    void zonesListWithFilters_ConRangoIgual_DeberiaSerValido() {
        // Given
        when(zonesJdbcRepository.getZonesWithFilters(
                anyInt(), anyInt(),
                any(), any(), any(), any(),
                eq(RiskLevel.MEDIUM), eq(RiskLevel.MEDIUM)))
                .thenReturn(Collections.emptyList());

        // When
        Mono<ZonesPaginatedDto> result = zonesService.zonesListWithFilters(
                1, 10, null, null, null, null, RiskLevel.MEDIUM, RiskLevel.MEDIUM);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(paginated -> paginated.getZones().isEmpty())
                .verifyComplete();
    }

    private Zones buildZone(int id) {
        Zones z = new Zones();
        z.setId(id);
        z.setName("Zona " + id);
        z.setProvince("Lima");
        z.setSecurityLevel(5);
        return z;
    }

    // Helper methods
    private Zones createMockZone() {
        return Zones.builder()
                .id(1)
                .name("Zona Test")
                .district("Miraflores")
                .province("Lima")
                .region("Lima")
                .country("Peru")
                .latitude(-12.1191)
                .longitude(-77.0292)
                .securityLevel(5)
                .active(true)
                .createdAt(LocalDateTime.now())
                .createdBy("admin")
                .build();
    }

    private ZonesDto createMockDto() {
        return ZonesDto.builder()
                .zoneCode(1)
                .name("Zona Test")
                .district("Miraflores")
                .province("Lima")
                .region("Lima")
                .country("Peru")
                .latitude(-12.1191)
                .longitude(-77.0292)
                .securityLevel(5)
                .active(true)
                .build();
    }

    private ZoneDatosCreateRequest createMockCreateRequest() {
        ZoneDatosCreateRequest request = new ZoneDatosCreateRequest();
        request.setCodzona(1);
        request.setNombre("Zona Test");
        request.setDistrito("Miraflores");
        request.setProvincia("Lima");
        request.setRegion("Lima");
        request.setPais("Peru");
        request.setLatitud(-12.1191);
        request.setLongitud(-77.0292);
        request.setNivelSeguridad(5);
        request.setUsuarioCreacion("admin");
        return request;
    }

    private ZoneDatosUpdateRequest createMockUpdateRequest() {
        ZoneDatosUpdateRequest request = new ZoneDatosUpdateRequest();
        request.setNombre("Zona Actualizada");
        request.setLatitud(-12.0464);
        request.setLongitud(-77.0428);
        request.setNivelSeguridad(7);
        request.setUsuarioActualizacion("admin");
        return request;
    }
}
