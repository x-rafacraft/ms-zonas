package pe.com.practicar.delegate.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import pe.com.practicar.business.ZonesService;
import pe.com.practicar.delegate.builder.ZonesMapper;
import pe.com.practicar.expose.controller.ZonesApiDelegate;
import pe.com.practicar.expose.schema.ZonePaginateResponse;
import pe.com.practicar.expose.schema.ZoneResponse;
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
        log.info("Obteniendo zonas - Página: {}, Tamaño: {}", paginaActual, tamanioPagina);
        
        return zonesService.zonesList(paginaActual, tamanioPagina)
                .map(zonesPaginatedDto -> {
                    List<ZoneResponse> zoneResponses = zonesPaginatedDto.getZones().stream()
                            .map(zonesMapper::zoneDtoToResponse)
                            .toList();
                    
                    ZonePaginateResponse response = new ZonePaginateResponse();
                    response.setZone(zoneResponses);
                    response.setPaginaActual(paginaActual);
                    response.setTamanioPagina(tamanioPagina);
                    
                    return response;
                })
                .doOnSuccess(response -> log.info("Zonas obtenidas exitosamente: {} registros", 
                        response.getZone().size()))
                .doOnError(error -> log.error("Error al obtener zonas", error));
    }
}
