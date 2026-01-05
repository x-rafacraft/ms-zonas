package pe.com.practicar.business;

import pe.com.practicar.business.dto.ZoneSummaryDto;
import pe.com.practicar.business.dto.ZonesDto;
import pe.com.practicar.business.dto.ZonesPaginatedDto;
import pe.com.practicar.expose.schema.ZoneDatosCreateRequest;
import pe.com.practicar.expose.schema.ZoneDatosUpdateRequest;
import reactor.core.publisher.Mono;

public interface ZonesService {

    Mono<ZonesPaginatedDto> zonesList(Integer currentPage, Integer pageSize);
    
    Mono<ZonesPaginatedDto> zonesListWithFilters(Integer currentPage, Integer pageSize, String province, String district, Integer securityLevel);
    
    Mono<ZonesDto> getZoneById(Integer zoneCode);
    
    Mono<ZonesDto> createZone(ZoneDatosCreateRequest createRequest);
    
    Mono<ZonesDto> updateZone(Integer zoneCode, ZoneDatosUpdateRequest updateRequest);
    
    Mono<ZonesDto> replaceZone(Integer zoneCode, ZoneDatosCreateRequest replaceRequest);
    
    Mono<Void> deleteZone(Integer zoneCode);
    
    Mono<ZoneSummaryDto> getZonesSummary();
}
