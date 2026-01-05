package pe.com.practicar.expose.controller;

import org.springframework.web.server.ServerWebExchange;
import pe.com.practicar.expose.schema.ZoneCreateRequest;
import pe.com.practicar.expose.schema.ZonePaginateResponse;
import pe.com.practicar.expose.schema.ZoneResponse;
import pe.com.practicar.expose.schema.ZoneUpdateRequest;
import reactor.core.publisher.Mono;

public interface ZonesApiDelegate {

    Mono<ZonePaginateResponse> obtenerZonas(Integer paginaActual, Integer tamanioPagina, ServerWebExchange exchange);
    
    Mono<ZonePaginateResponse> obtenerZonasConFiltros(Integer paginaActual, Integer tamanioPagina, 
                                                       String provincia, String distrito, Integer nivelSeguridad, 
                                                       ServerWebExchange exchange);
    
    Mono<ZoneResponse> obtenerZonaPorId(Integer codigoZona, ServerWebExchange exchange);
    
    Mono<ZoneResponse> crearZona(ZoneCreateRequest request, ServerWebExchange exchange);
    
    Mono<ZoneResponse> actualizarZona(Integer codigoZona, ZoneUpdateRequest request, ServerWebExchange exchange);
    
    Mono<ZoneResponse> reemplazarZona(Integer codigoZona, ZoneCreateRequest request, ServerWebExchange exchange);
    
    Mono<Void> eliminarZona(Integer codigoZona, ServerWebExchange exchange);
}
