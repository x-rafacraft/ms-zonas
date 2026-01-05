package pe.com.practicar.business.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import pe.com.practicar.business.ZonesService;
import pe.com.practicar.business.dto.ZoneSummaryByLevelDto;
import pe.com.practicar.business.dto.ZoneSummaryDto;
import pe.com.practicar.business.dto.ZonesDto;
import pe.com.practicar.business.dto.ZonesPaginatedDto;
import pe.com.practicar.business.exception.BusinessErrorCodes;
import pe.com.practicar.business.exception.BusinessException;
import pe.com.practicar.business.validator.PaginationValidator;
import pe.com.practicar.business.validator.ZoneCreateRequestValidator;
import pe.com.practicar.business.validator.ZoneIdValidator;
import pe.com.practicar.business.validator.ZoneUpdateRequestValidator;
import pe.com.practicar.expose.schema.ZoneDatosCreateRequest;
import pe.com.practicar.expose.schema.ZoneDatosUpdateRequest;
import pe.com.practicar.mapper.ZoneMapper;
import pe.com.practicar.repository.ZonesJdbcRepository;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@Slf4j
@RequiredArgsConstructor
public class ZonesServiceImpl implements ZonesService {

    private final ZonesJdbcRepository zonesJdbcRepository;
    private final ZoneMapper zoneMapper;

    @Override
    public Mono<ZonesPaginatedDto> zonesList(Integer currentPage, Integer pageSize) {
        return PaginationValidator.validatePagination(currentPage, pageSize)
                .flatMap(valid -> Mono.fromCallable(() -> zonesJdbcRepository.getZonesPaginated(currentPage, pageSize)))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(monoZones -> monoZones)
                .map(zones -> zones.stream()
                        .map(zoneMapper::convertToZoneResponse)
                        .toList())
                .map(zonesDtoList -> ZonesPaginatedDto.builder()
                        .zones(zonesDtoList)
                        .currentPage(currentPage)
                        .pageSize(pageSize)
                        .build()
                );
    }

    @Override
    public Mono<ZonesPaginatedDto> zonesListWithFilters(Integer currentPage, Integer pageSize, String province, String district, Integer securityLevel) {
        return PaginationValidator.validatePaginationWithFilters(currentPage, pageSize, province, district, securityLevel)
                .flatMap(valid -> Mono.fromCallable(() -> zonesJdbcRepository.getZonesWithFilters(currentPage, pageSize, province, district, securityLevel)))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(monoZones -> monoZones)
                .map(zones -> zones.stream()
                        .map(zoneMapper::convertToZoneResponse)
                        .toList())
                .map(zonesDtoList -> ZonesPaginatedDto.builder()
                        .zones(zonesDtoList)
                        .currentPage(currentPage)
                        .pageSize(pageSize)
                        .build()
                );
    }

    @Override
    public Mono<ZonesDto> getZoneById(Integer zoneCode) {
        return ZoneIdValidator.validate(zoneCode)
                .flatMap(valid -> Mono.fromCallable(() -> zonesJdbcRepository.getZoneById(zoneCode)))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(monoZone -> monoZone)
                .map(zoneMapper::convertToZoneResponse)
                .switchIfEmpty(Mono.error(
                        BusinessException.createException(HttpStatus.NOT_FOUND, BusinessErrorCodes.ZONE_NOT_FOUND)
                ));
    }

    @Override
    public Mono<ZonesDto> createZone(ZoneDatosCreateRequest createRequest) {
        return ZoneCreateRequestValidator.validate(createRequest)
                .flatMap(valid -> zonesJdbcRepository.existsByCodzona(createRequest.getCodzona()))
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(BusinessException.builder()
                                .httpStatus(HttpStatus.CONFLICT)
                                .code(BusinessErrorCodes.ZONE_ALREADY_EXISTS.getCode())
                                .type(BusinessErrorCodes.ZONE_ALREADY_EXISTS.getTitle())
                                .message("Ya existe una zona con el código: " + createRequest.getCodzona())
                                .build());
                    }
                    return zonesJdbcRepository.existsByNombre(createRequest.getNombre());
                })
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(BusinessException.builder()
                                .httpStatus(HttpStatus.CONFLICT)
                                .code(BusinessErrorCodes.ZONE_ALREADY_EXISTS.getCode())
                                .type(BusinessErrorCodes.ZONE_ALREADY_EXISTS.getTitle())
                                .message("Ya existe una zona con el nombre: " + createRequest.getNombre())
                                .build());
                    }
                    return zonesJdbcRepository.existsByCoordinates(createRequest.getLatitud(), createRequest.getLongitud());
                })
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(BusinessException.builder()
                                .httpStatus(HttpStatus.CONFLICT)
                                .code(BusinessErrorCodes.ZONE_ALREADY_EXISTS.getCode())
                                .type(BusinessErrorCodes.ZONE_ALREADY_EXISTS.getTitle())
                                .message("Ya existe una zona con las coordenadas: latitud=" + createRequest.getLatitud() + ", longitud=" + createRequest.getLongitud())
                                .build());
                    }
                    return Mono.fromCallable(() -> zonesJdbcRepository.createZone(createRequest));
                })
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(monoZone -> monoZone)
                .map(zoneMapper::convertToZoneResponse)
                .onErrorMap(e -> {
                    if (e instanceof BusinessException) {
                        return e;
                    }
                    log.error("Error al crear zona", e);
                    return BusinessException.createException(HttpStatus.INTERNAL_SERVER_ERROR, BusinessErrorCodes.ZONE_CREATION_FAILED);
                });
    }

    @Override
    public Mono<ZonesDto> updateZone(Integer zoneCode, ZoneDatosUpdateRequest updateRequest) {
        return ZoneIdValidator.validate(zoneCode)
                .flatMap(valid -> ZoneUpdateRequestValidator.validate(updateRequest))
                .flatMap(valid -> {
                    // Validar nombre duplicado si se está actualizando
                    if (updateRequest.getNombre() != null && !updateRequest.getNombre().isEmpty()) {
                        return zonesJdbcRepository.existsByNombreExcludingId(updateRequest.getNombre(), zoneCode)
                                .flatMap(exists -> {
                                    if (exists) {
                                        return Mono.error(BusinessException.builder()
                                                .httpStatus(HttpStatus.CONFLICT)
                                                .code(BusinessErrorCodes.ZONE_ALREADY_EXISTS.getCode())
                                                .type(BusinessErrorCodes.ZONE_ALREADY_EXISTS.getTitle())
                                                .message("Ya existe otra zona con el nombre: " + updateRequest.getNombre())
                                                .build());
                                    }
                                    return Mono.just(true);
                                });
                    }
                    return Mono.just(true);
                })
                .flatMap(valid -> {
                    // Validar coordenadas duplicadas si se están actualizando ambas
                    if (updateRequest.getLatitud() != null && updateRequest.getLongitud() != null) {
                        return zonesJdbcRepository.existsByCoordinatesExcludingId(
                                updateRequest.getLatitud(), updateRequest.getLongitud(), zoneCode)
                                .flatMap(exists -> {
                                    if (exists) {
                                        return Mono.error(BusinessException.builder()
                                                .httpStatus(HttpStatus.CONFLICT)
                                                .code(BusinessErrorCodes.ZONE_ALREADY_EXISTS.getCode())
                                                .type(BusinessErrorCodes.ZONE_ALREADY_EXISTS.getTitle())
                                                .message("Ya existe otra zona con las coordenadas: latitud=" 
                                                        + updateRequest.getLatitud() + ", longitud=" + updateRequest.getLongitud())
                                                .build());
                                    }
                                    return Mono.just(true);
                                });
                    }
                    return Mono.just(true);
                })
                .flatMap(valid -> Mono.fromCallable(() -> zonesJdbcRepository.updateZone(zoneCode, updateRequest)))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(monoZone -> monoZone)
                .map(zoneMapper::convertToZoneResponse)
                .switchIfEmpty(Mono.error(
                        BusinessException.createException(HttpStatus.NOT_FOUND, BusinessErrorCodes.ZONE_NOT_FOUND)
                ))
                .onErrorMap(e -> {
                    if (e instanceof BusinessException) {
                        return e;
                    }
                    log.error("Error al actualizar zona", e);
                    return BusinessException.createException(HttpStatus.INTERNAL_SERVER_ERROR, BusinessErrorCodes.ZONE_UPDATE_FAILED);
                });
    }

    @Override
    public Mono<ZonesDto> replaceZone(Integer zoneCode, ZoneDatosCreateRequest replaceRequest) {
        return ZoneIdValidator.validate(zoneCode)
                .flatMap(valid -> ZoneCreateRequestValidator.validate(replaceRequest))
                .flatMap(valid -> Mono.fromCallable(() -> zonesJdbcRepository.replaceZone(zoneCode, replaceRequest)))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(monoZone -> monoZone)
                .map(zoneMapper::convertToZoneResponse)
                .switchIfEmpty(Mono.error(
                        BusinessException.createException(HttpStatus.NOT_FOUND, BusinessErrorCodes.ZONE_NOT_FOUND)
                ))
                .onErrorMap(e -> {
                    if (e instanceof BusinessException) {
                        return e;
                    }
                    log.error("Error al reemplazar zona", e);
                    return BusinessException.createException(HttpStatus.INTERNAL_SERVER_ERROR, BusinessErrorCodes.ZONE_UPDATE_FAILED);
                });
    }

    @Override
    public Mono<Void> deleteZone(Integer zoneCode) {
        return ZoneIdValidator.validate(zoneCode)
                .flatMap(valid -> Mono.fromCallable(() -> zonesJdbcRepository.deleteZone(zoneCode)))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(monoResult -> monoResult)
                .flatMap(rowsAffected -> {
                    if (rowsAffected == 0) {
                        return Mono.error(BusinessException.createException(HttpStatus.NOT_FOUND, BusinessErrorCodes.ZONE_NOT_FOUND));
                    }
                    return Mono.<Void>empty();
                })
                .onErrorResume(e -> {
                    if (e instanceof BusinessException) {
                        return Mono.error(e);
                    }
                    log.error("Error al eliminar zona", e);
                    return Mono.error(BusinessException.createException(HttpStatus.INTERNAL_SERVER_ERROR, BusinessErrorCodes.ZONE_DELETE_FAILED));
                });
    }

    @Override
    public Mono<ZoneSummaryDto> getZonesSummary() {
        return Mono.zip(
                zonesJdbcRepository.getZonesSummaryBySecurityLevel(),
                zonesJdbcRepository.getTotalZonesCount()
        )
        .map(tuple -> {
            var summaryByLevel = tuple.getT1().stream()
                    .map(summary -> ZoneSummaryByLevelDto.builder()
                            .nivelSeguridad(summary.getSecurityLevel())
                            .cantidad(summary.getCount())
                            .build())
                    .toList();

            return ZoneSummaryDto.builder()
                    .resumenPorNivel(summaryByLevel)
                    .totalZonas(tuple.getT2())
                    .build();
        })
        .onErrorResume(e -> {
            log.error("Error al obtener resumen de zonas", e);
            return Mono.error(BusinessException.builder()
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .code(BusinessErrorCodes.ZONE_NOT_FOUND.getCode())
                    .type(BusinessErrorCodes.ZONE_NOT_FOUND.getTitle())
                    .message("Error al obtener el resumen de zonas")
                    .build());
        });
    }
}
