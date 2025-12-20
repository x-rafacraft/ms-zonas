package pe.com.practicar.expose.controller;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import pe.com.practicar.expose.schema.ZonePaginateResponse;
import pe.com.practicar.expose.schema.ZoneResponse;
import pe.com.practicar.expose.schema.ZoneUpdateRequest;
import reactor.core.publisher.Mono;

@Validated
@RestController
public interface ZonesApi {

    @GetMapping("/zones")
    @ResponseStatus(HttpStatus.OK)
    Mono<ZonePaginateResponse> obtenerZonas(
            @RequestParam(required = false) Integer paginaActual,
            @RequestParam(required = false) Integer tamanioPagina,
            final ServerWebExchange exchange);

    @PatchMapping("/zones/{codigoZona}")
    @ResponseStatus(HttpStatus.OK)
    Mono<ZoneResponse> actualizarZona(
            @PathVariable Integer codigoZona,
            @RequestBody ZoneUpdateRequest request,
            final ServerWebExchange exchange);

}
