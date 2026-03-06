package pe.com.practicar.delegate.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import pe.com.practicar.business.ZonesService;
import pe.com.practicar.business.exception.BusinessErrorCodes;
import pe.com.practicar.business.exception.BusinessException;
import pe.com.practicar.mapper.ZoneMapper;
import pe.com.practicar.expose.controller.ZonesApiDelegate;
import pe.com.practicar.expose.schema.ZoneCreateRequest;
import pe.com.practicar.expose.schema.ZonePaginateResponse;
import pe.com.practicar.expose.schema.ZoneResponse;
import pe.com.practicar.business.model.RiskLevel;
import pe.com.practicar.expose.schema.ZoneSummaryByLevelResponse;
import pe.com.practicar.expose.schema.ZoneSummaryResponse;
import pe.com.practicar.expose.schema.ZoneUpdateRequest;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ZonesApiDelegateImpl implements ZonesApiDelegate {

    private final ZonesService zonesService;
    private final ZoneMapper zoneMapper;

    @Override
    public Mono<ZonePaginateResponse> obtenerZonas(Integer paginaActual, Integer tamanioPagina, ServerWebExchange exchange) {
        return zonesService.zonesList(paginaActual, tamanioPagina)
                .map(zonesPaginatedDto -> {
                    List<ZoneResponse> zoneResponses = zonesPaginatedDto.getZones().stream()
                            .map(zoneMapper::zoneDtoToResponse)
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
                                                             String ciudad, String minRiskStr, String maxRiskStr,
                                                             ServerWebExchange exchange) {
        RiskLevel minRisk = parseRiskLevel(minRiskStr);
        RiskLevel maxRisk = parseRiskLevel(maxRiskStr);
        return zonesService.zonesListWithFilters(paginaActual, tamanioPagina, provincia, distrito, nivelSeguridad, ciudad, minRisk, maxRisk)
                .map(zonesPaginatedDto -> {
                    List<ZoneResponse> zoneResponses = zonesPaginatedDto.getZones().stream()
                            .map(zoneMapper::zoneDtoToResponse)
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

    private RiskLevel parseRiskLevel(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return RiskLevel.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw BusinessException.builder()
                    .httpStatus(org.springframework.http.HttpStatus.BAD_REQUEST)
                    .code(BusinessErrorCodes.INVALID_RISK_LEVEL.getCode())
                    .type(BusinessErrorCodes.INVALID_RISK_LEVEL.getTitle())
                    .message("Valor de riesgo inválido: '" + value + "'. Los valores permitidos son: LOW, MEDIUM, HIGH.")
                    .build();
        }
    }

    @Override
    public Mono<ZoneResponse> obtenerZonaPorId(Integer codigoZona, ServerWebExchange exchange) {
        return zonesService.getZoneById(codigoZona)
                .map(zoneMapper::zoneDtoToResponse);
    }

    @Override
    public Mono<ZoneResponse> crearZona(ZoneCreateRequest request, ServerWebExchange exchange) {
        return zonesService.createZone(request.getDatos())
                .map(zoneMapper::zoneDtoToResponse);
    }

    @Override
    public Mono<ZoneResponse> actualizarZona(Integer codigoZona, ZoneUpdateRequest request, ServerWebExchange exchange) {
        return zonesService.updateZone(codigoZona, request.getDatos())
                .map(zoneMapper::zoneDtoToResponse);
    }

    @Override
    public Mono<ZoneResponse> reemplazarZona(Integer codigoZona, ZoneCreateRequest request, ServerWebExchange exchange) {
        return zonesService.replaceZone(codigoZona, request.getDatos())
                .map(zoneMapper::zoneDtoToResponse);
    }

    @Override
    public Mono<Void> eliminarZona(Integer codigoZona, ServerWebExchange exchange) {
        return zonesService.deleteZone(codigoZona);
    }

    @Override
    public Mono<ZoneSummaryResponse> obtenerResumenZonas(ServerWebExchange exchange) {
        return zonesService.getZonesSummary()
                .map(summaryDto -> {
                    List<ZoneSummaryByLevelResponse> resumenPorNivel = summaryDto.getResumenPorNivel().stream()
                            .map(byLevel -> ZoneSummaryByLevelResponse.builder()
                                    .nivelSeguridad(byLevel.getNivelSeguridad())
                                    .cantidad(byLevel.getCantidad())
                                    .build())
                            .toList();

                    return ZoneSummaryResponse.builder()
                            .resumenPorNivel(resumenPorNivel)
                            .totalZonas(summaryDto.getTotalZonas())
                            .build();
                });
    }
}
