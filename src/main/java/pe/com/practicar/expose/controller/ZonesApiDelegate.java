package pe.com.practicar.expose.controller;

import org.springframework.web.server.ServerWebExchange;
import pe.com.practicar.expose.schema.ZonePaginateResponse;
import reactor.core.publisher.Mono;

public interface ZonesApiDelegate {

    Mono<ZonePaginateResponse> obtenerZonas(Integer paginaActual, Integer tamanioPagina, ServerWebExchange exchange);
}
