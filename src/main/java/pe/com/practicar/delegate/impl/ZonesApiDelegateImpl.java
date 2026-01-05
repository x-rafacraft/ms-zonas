package pe.com.practicar.delegate.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import pe.com.practicar.business.ZonesService;
import pe.com.practicar.delegate.builder.ZonesMapper;
import pe.com.practicar.expose.controller.ZonesApiDelegate;
import pe.com.practicar.expose.schema.ZoneCreateRequest;
import pe.com.practicar.expose.schema.ZonePaginateResponse;
import pe.com.practicar.expose.schema.ZoneResponse;
import pe.com.practicar.expose.schema.ZoneUpdateRequest;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ZonesApiDelegateImpl implements ZonesApiDelegate {

    private final ZonesService zonesService;
    private final ZonesMapper zonesMapper;

    @Override
    public Mono<ZonePaginateResponse> obtenerZonas(Integer paginaActual, Integer tamanioPagina, ServerWebExchange exchange) {
        return zonesService.zonesList(paginaActual, tamanioPagina)
                .map(zonesPaginatedDto -> {
                    List<ZoneResponse> zoneResponses = zonesPaginatedDto.getZones().stream()
                            .map(zonesMapper::zoneDtoToResponse)
                            .toList();
                    
                    boolean existeSiguientePagina = zoneResponses.size() == tamanioPagina;
                    
                    ZonePaginateResponse response = new ZonePaginateResponse();
                    response.setZone(zoneResponses);
                    response.setPaginaActual(paginaActual);
                    response.setTamanioPagina(tamanioPagina);
                    response.setExisteSiguientePagina(existeSiguientePagina);
                    
                    return response;
                });
    }

    @Override
    public Mono<ZonePaginateResponse> obtenerZonasConFiltros(Integer paginaActual, Integer tamanioPagina, 
                                                             String provincia, String distrito, Integer nivelSeguridad, 
                                                             ServerWebExchange exchange) {
        return zonesService.zonesListWithFilters(paginaActual, tamanioPagina, provincia, distrito, nivelSeguridad)
                .map(zonesPaginatedDto -> {
                    List<ZoneResponse> zoneResponses = zonesPaginatedDto.getZones().stream()
                            .map(zonesMapper::zoneDtoToResponse)
                            .toList();
                    
                    boolean existeSiguientePagina = zoneResponses.size() == tamanioPagina;
                    
                    ZonePaginateResponse response = new ZonePaginateResponse();
                    response.setZone(zoneResponses);
                    response.setPaginaActual(paginaActual);
                    response.setTamanioPagina(tamanioPagina);
                    response.setExisteSiguientePagina(existeSiguientePagina);
                    
                    return response;
                });
    }

    @Override
    public Mono<ZoneResponse> obtenerZonaPorId(Integer codigoZona, ServerWebExchange exchange) {
        return zonesService.getZoneById(codigoZona)
                .map(zonesMapper::zoneDtoToResponse);
    }

    @Override
    public Mono<ZoneResponse> crearZona(ZoneCreateRequest request, ServerWebExchange exchange) {
        return zonesService.createZone(request.getDatos())
                .map(zonesMapper::zoneDtoToResponse);
    }

    @Override
    public Mono<ZoneResponse> actualizarZona(Integer codigoZona, ZoneUpdateRequest request, ServerWebExchange exchange) {
        return zonesService.updateZone(codigoZona, request.getDatos())
                .map(zonesMapper::zoneDtoToResponse);
    }

    @Override
    public Mono<ZoneResponse> reemplazarZona(Integer codigoZona, ZoneCreateRequest request, ServerWebExchange exchange) {
        return zonesService.replaceZone(codigoZona, request.getDatos())
                .map(zonesMapper::zoneDtoToResponse);
    }

    @Override
    public Mono<Void> eliminarZona(Integer codigoZona, ServerWebExchange exchange) {
        return zonesService.deleteZone(codigoZona);
    }
}
