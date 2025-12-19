package pe.com.practicar.business;

import pe.com.practicar.business.dto.ZonesPaginatedDto;
import reactor.core.publisher.Mono;

public interface ZonesService {

    Mono<ZonesPaginatedDto> zonesList(Integer currentPage, Integer pageSize);
}
