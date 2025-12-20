package pe.com.practicar.delegate.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.com.practicar.business.ZonesService;
import pe.com.practicar.business.dto.ZonesDto;
import pe.com.practicar.business.dto.ZonesPaginatedDto;
import pe.com.practicar.delegate.builder.ZonesMapper;
import pe.com.practicar.expose.schema.ZoneCreateRequest;
import pe.com.practicar.expose.schema.ZoneDatosCreateRequest;
import pe.com.practicar.expose.schema.ZoneResponse;
import pe.com.practicar.expose.schema.ZoneUpdateRequest;
import pe.com.practicar.expose.schema.ZoneDatosUpdateRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ZonesApiDelegateImplTest {

    @Mock
    private ZonesService zonesService;

    @Mock
    private ZonesMapper zonesMapper;

    @InjectMocks
    private ZonesApiDelegateImpl zonesApiDelegate;

    @Test
    void obtenerZonas_DeberiaRetornarRespuestaPaginada() {
        // Given
        ZonesDto dto = ZonesDto.builder()
                .zoneCode(1)
                .names("Zona Test")
                .build();
        
        ZonesPaginatedDto paginatedDto = ZonesPaginatedDto.builder()
                .zones(Arrays.asList(dto))
                .currentPage(1)
                .pageSize(10)
                .build();
        
        ZoneResponse response = new ZoneResponse();
        response.setNombre("Zona Test");
        
        when(zonesService.zonesList(anyInt(), anyInt())).thenReturn(Mono.just(paginatedDto));
        when(zonesMapper.zoneDtoToResponse(any())).thenReturn(response);

        // When
        Mono<?> result = zonesApiDelegate.obtenerZonas(1, 10, null);

        // Then
        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void crearZona_DeberiaRetornarZonaCreada() {
        // Given
        ZoneDatosCreateRequest datos = new ZoneDatosCreateRequest();
        datos.setNombre("Nueva Zona");
        
        ZoneCreateRequest request = new ZoneCreateRequest();
        request.setDatos(datos);
        
        ZonesDto dto = ZonesDto.builder()
                .zoneCode(1)
                .names("Nueva Zona")
                .build();
        
        ZoneResponse response = new ZoneResponse();
        response.setNombre("Nueva Zona");
        
        when(zonesService.createZone(any())).thenReturn(Mono.just(dto));
        when(zonesMapper.zoneDtoToResponse(any())).thenReturn(response);

        // When
        Mono<ZoneResponse> result = zonesApiDelegate.crearZona(request, null);

        // Then
        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void actualizarZona_DeberiaRetornarZonaActualizada() {
        // Given
        ZoneDatosUpdateRequest datos = new ZoneDatosUpdateRequest();
        datos.setLatitud(-12.0464);
        
        ZoneUpdateRequest request = new ZoneUpdateRequest();
        request.setDatos(datos);
        
        ZonesDto dto = ZonesDto.builder()
                .zoneCode(1)
                .latitudes(-12.0464)
                .build();
        
        ZoneResponse response = new ZoneResponse();
        response.setLatitud(-12.0464);
        
        when(zonesService.updateZone(anyInt(), any())).thenReturn(Mono.just(dto));
        when(zonesMapper.zoneDtoToResponse(any())).thenReturn(response);

        // When
        Mono<ZoneResponse> result = zonesApiDelegate.actualizarZona(1, request, null);

        // Then
        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();
    }
}
