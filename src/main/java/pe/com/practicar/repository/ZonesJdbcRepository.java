package pe.com.practicar.repository;

import pe.com.practicar.expose.schema.ZoneDatosCreateRequest;
import pe.com.practicar.expose.schema.ZoneDatosUpdateRequest;
import pe.com.practicar.repository.model.Zones;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ZonesJdbcRepository {

    Mono<List<Zones>> getZonesPaginated(Integer currentPage, Integer pageSize);
    
    Mono<List<Zones>> getZonesWithFilters(Integer currentPage, Integer pageSize, String province, String district, Integer securityLevel);
    
    Mono<Zones> getZoneById(Integer zoneCode);
    
    Mono<Zones> createZone(ZoneDatosCreateRequest createRequest);
    
    Mono<Zones> updateZone(Integer zoneCode, ZoneDatosUpdateRequest updateRequest);
    
    Mono<Zones> replaceZone(Integer zoneCode, ZoneDatosCreateRequest replaceRequest);
    
    Mono<Integer> deleteZone(Integer zoneCode);
    
    Mono<Boolean> existsByNombre(String nombre);
    
    Mono<Boolean> existsByCoordinates(Double latitud, Double longitud);
    
    Mono<Boolean> existsByNombreExcludingId(String nombre, Integer zoneId);
    
    Mono<Boolean> existsByCoordinatesExcludingId(Double latitud, Double longitud, Integer zoneId);
    
    Mono<Boolean> existsByCodzona(Integer codzona);
}
