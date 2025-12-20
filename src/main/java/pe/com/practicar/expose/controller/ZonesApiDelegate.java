package pe.com.practicar.expose.controller;

import org.springframework.web.server.ServerWebExchange;
import pe.com.practicar.expose.schema.ZoneCreateRequest;
import pe.com.practicar.expose.schema.ZonePaginateResponse;
import pe.com.practicar.expose.schema.ZoneResponse;
import pe.com.practicar.expose.schema.ZoneUpdateRequest;
import reactor.core.publisher.Mono;

public interface ZonesApiDelegate {

    Mono<ZonePaginateResponse> obtenerZonas(Integer paginaActual, Integer tamanioPagina, ServerWebExchange exchange);
    
    Mono<ZoneResponse> crearZona(ZoneCreateRequest request, ServerWebExchange exchange);
    
    Mono<ZoneResponse> actualizarZona(Integer codigoZona, ZoneUpdateRequest request, ServerWebExchange exchange);
}
