package pe.com.practicar.business.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.com.practicar.mapper.ZoneMapper;
import pe.com.practicar.repository.ZonesJdbcRepository;
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
}
