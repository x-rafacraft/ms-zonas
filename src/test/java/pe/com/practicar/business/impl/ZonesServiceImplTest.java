package pe.com.practicar.business.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.com.practicar.mapper.ZoneMapper;
import pe.com.practicar.repository.ZonesJdbcRepository;
import pe.com.practicar.repository.model.ZoneSummaryByLevel;
import pe.com.practicar.repository.model.Zones;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
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
                .thenReturn(Mono.just(mockZones));

        // When
        Mono<?> result = zonesService.zonesList(1, 10);

        // Then
        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void zonesList_ConPaginaInvalida_DeberiaManejarlo() {
        // Given
        when(zonesJdbcRepository.getZonesPaginated(anyInt(), anyInt()))
                .thenReturn(Mono.just(Arrays.asList()));

        // When
        Mono<?> result = zonesService.zonesList(0, 10);

        // Then
        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void zonesListWithFilters_DeberiaRetornarListaFiltrada() {
        // Given
        List<Zones> mockZones = Arrays.asList(new Zones(), new Zones());
        when(zonesJdbcRepository.getZonesWithFilters(anyInt(), anyInt(), 
                any(), any(), anyInt()))
                .thenReturn(Mono.just(mockZones));

        // When
        Mono<?> result = zonesService.zonesListWithFilters(1, 10, "Lima", "Miraflores", 4);

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
                .thenReturn(Mono.just(Arrays.asList(summary1, summary2)));
        when(zonesJdbcRepository.getTotalZonesCount())
                .thenReturn(Mono.just(15L));

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
}
