package pe.com.practicar.expose.controller;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import pe.com.practicar.expose.schema.ZonePaginateResponse;
import reactor.core.publisher.Mono;

@Validated
@RestController
public interface ZonesApi {

    @RequestMapping("/zones")
    @ResponseStatus(HttpStatus.OK)
    Mono<ZonePaginateResponse> obtenerZonas(
            Integer paginaActual,
            Integer tamanioPagina,
            final ServerWebExchange exchange);

}
