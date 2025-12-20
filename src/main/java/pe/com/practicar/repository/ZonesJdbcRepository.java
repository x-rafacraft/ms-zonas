package pe.com.practicar.repository;

import pe.com.practicar.expose.schema.ZoneDatosUpdateRequest;
import pe.com.practicar.repository.model.Zones;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ZonesJdbcRepository {

    Mono<List<Zones>> getZonesPaginated(Integer currentPage, Integer pageSize);
    
    Mono<Zones> updateZone(Integer zoneCode, ZoneDatosUpdateRequest updateRequest);
}
