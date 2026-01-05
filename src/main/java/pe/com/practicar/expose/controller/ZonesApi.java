package pe.com.practicar.expose.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import pe.com.practicar.expose.schema.ZoneCreateRequest;
import pe.com.practicar.expose.schema.ZonePaginateResponse;
import pe.com.practicar.expose.schema.ZoneResponse;
import pe.com.practicar.expose.schema.ZoneSummaryResponse;
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
            @RequestParam(required = false) String provincia,
            @RequestParam(required = false) String distrito,
            @RequestParam(required = false) Integer nivelSeguridad,
            final ServerWebExchange exchange);

    @GetMapping("/zones/{codigoZona}")
    @ResponseStatus(HttpStatus.OK)
    Mono<ZoneResponse> obtenerZonaPorId(
            @PathVariable Integer codigoZona,
            final ServerWebExchange exchange);

    @PostMapping("/zones")
    @ResponseStatus(HttpStatus.CREATED)
    Mono<ZoneResponse> crearZona(
            @Valid @RequestBody ZoneCreateRequest request,
            final ServerWebExchange exchange);

    @PatchMapping("/zones/{codigoZona}")
    @ResponseStatus(HttpStatus.OK)
    Mono<ZoneResponse> actualizarZona(
            @PathVariable Integer codigoZona,
            @Valid @RequestBody ZoneUpdateRequest request,
            final ServerWebExchange exchange);

    @PutMapping("/zones/{codigoZona}")
    @ResponseStatus(HttpStatus.OK)
    Mono<ZoneResponse> reemplazarZona(
            @PathVariable Integer codigoZona,
            @Valid @RequestBody ZoneCreateRequest request,
            final ServerWebExchange exchange);

    @DeleteMapping("/zones/{codigoZona}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    Mono<Void> eliminarZona(
            @PathVariable Integer codigoZona,
            final ServerWebExchange exchange);

    @GetMapping("/zones/summary")
    @ResponseStatus(HttpStatus.OK)
    Mono<ZoneSummaryResponse> obtenerResumenZonas(final ServerWebExchange exchange);

}
