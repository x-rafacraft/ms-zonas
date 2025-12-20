package pe.com.practicar.expose.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import pe.com.practicar.expose.schema.ZonePaginateResponse;
import pe.com.practicar.expose.schema.ZoneResponse;
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

    public Mono<ZonePaginateResponse> obtenerZonas(
            Integer paginaActual,
            Integer tamanioPagina,
            final ServerWebExchange exchange) {
        return getDelegate().obtenerZonas(paginaActual, tamanioPagina, exchange);
    }

    @Override
    public Mono<ZoneResponse> actualizarZona(
            Integer codigoZona,
            ZoneUpdateRequest request,
            ServerWebExchange exchange) {
        return getDelegate().actualizarZona(codigoZona, request, exchange);
    }

}
