package pe.com.practicar.business;

import pe.com.practicar.business.dto.ZonesDto;
import pe.com.practicar.business.dto.ZonesPaginatedDto;
import pe.com.practicar.expose.schema.ZoneDatosCreateRequest;
import pe.com.practicar.expose.schema.ZoneDatosUpdateRequest;
import reactor.core.publisher.Mono;

public interface ZonesService {

    Mono<ZonesPaginatedDto> zonesList(Integer currentPage, Integer pageSize);
    
    Mono<ZonesDto> createZone(ZoneDatosCreateRequest createRequest);
    
    Mono<ZonesDto> updateZone(Integer zoneCode, ZoneDatosUpdateRequest updateRequest);
}
