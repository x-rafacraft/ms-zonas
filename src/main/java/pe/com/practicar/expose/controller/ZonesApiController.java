package pe.com.practicar.expose.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import pe.com.practicar.expose.schema.ZoneCreateRequest;
import pe.com.practicar.expose.schema.ZonePaginateResponse;
import pe.com.practicar.expose.schema.ZoneResponse;
import pe.com.practicar.expose.schema.ZoneSummaryResponse;
import pe.com.practicar.expose.schema.ZoneUpdateRequest;
import reactor.core.publisher.Mono;

@RestController
public class ZonesApiController implements ZonesApi {

    private final ZonesApiDelegate delegate;

    public ZonesApiController(@Autowired(required = false) ZonesApiDelegate delegate) {
        this.delegate = delegate;
    }

    public ZonesApiDelegate getDelegate() {
        return delegate;
    }

    @Override
    public Mono<ZonePaginateResponse> obtenerZonas(
            Integer paginaActual,
            Integer tamanioPagina,
            String provincia,
            String distrito,
            Integer nivelSeguridad,
            final ServerWebExchange exchange) {
        if (provincia != null || distrito != null || nivelSeguridad != null) {
            return getDelegate().obtenerZonasConFiltros(paginaActual, tamanioPagina, provincia, distrito, nivelSeguridad, exchange);
        }
        return getDelegate().obtenerZonas(paginaActual, tamanioPagina, exchange);
    }

    @Override
    public Mono<ZoneResponse> obtenerZonaPorId(
            Integer codigoZona,
            final ServerWebExchange exchange) {
        return getDelegate().obtenerZonaPorId(codigoZona, exchange);
    }

    @Override
    public Mono<ZoneResponse> crearZona(
            ZoneCreateRequest request,
            ServerWebExchange exchange) {
        return getDelegate().crearZona(request, exchange);
    }

    @Override
    public Mono<ZoneResponse> actualizarZona(
            Integer codigoZona,
            ZoneUpdateRequest request,
            ServerWebExchange exchange) {
        return getDelegate().actualizarZona(codigoZona, request, exchange);
    }

    @Override
    public Mono<ZoneResponse> reemplazarZona(
            Integer codigoZona,
            ZoneCreateRequest request,
            ServerWebExchange exchange) {
        return getDelegate().reemplazarZona(codigoZona, request, exchange);
    }

    @Override
    public Mono<Void> eliminarZona(
            Integer codigoZona,
            ServerWebExchange exchange) {
        return getDelegate().eliminarZona(codigoZona, exchange);
    }

    @Override
    public Mono<ZoneSummaryResponse> obtenerResumenZonas(ServerWebExchange exchange) {
        return getDelegate().obtenerResumenZonas(exchange);
    }

}
