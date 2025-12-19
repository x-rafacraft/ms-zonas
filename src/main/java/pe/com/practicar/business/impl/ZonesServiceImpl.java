package pe.com.practicar.business.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.com.practicar.business.ZonesService;
import pe.com.practicar.business.dto.ZonesPaginatedDto;
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
        return Mono.fromCallable(() -> zonesJdbcRepository.getZonesPaginated(currentPage, pageSize))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(monoZones -> monoZones)
                .map(zones -> zones.stream()
                        .map(zoneMapper::convertToZoneResponse)
                        .toList())
                .map(zonesDtoList -> ZonesPaginatedDto.builder()
                        .zones(zonesDtoList)
                        .build()
                );
    }
}
