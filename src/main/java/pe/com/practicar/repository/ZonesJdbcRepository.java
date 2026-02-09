package pe.com.practicar.repository;

import pe.com.practicar.expose.schema.ZoneDatosCreateRequest;
import pe.com.practicar.expose.schema.ZoneDatosUpdateRequest;
import pe.com.practicar.repository.model.ZoneSummaryByLevel;
import pe.com.practicar.repository.model.Zones;

import java.util.List;
import java.util.Optional;

public interface ZonesJdbcRepository {

    List<Zones> getZonesPaginated(Integer currentPage, Integer pageSize);
    
    List<Zones> getZonesWithFilters(Integer currentPage, Integer pageSize, String province, String district, Integer securityLevel);
    
    Optional<Zones> getZoneById(Integer zoneCode);
    
    Zones createZone(ZoneDatosCreateRequest createRequest);
    
    Optional<Zones> updateZone(Integer zoneCode, ZoneDatosUpdateRequest updateRequest);
    
    Optional<Zones> replaceZone(Integer zoneCode, ZoneDatosCreateRequest replaceRequest);
    
    int deleteZone(Integer zoneCode);
    
    boolean existsByNombre(String nombre);
    
    boolean existsByCoordinates(Double latitud, Double longitud);
    
    boolean existsByNombreExcludingId(String nombre, Integer zoneId);
    
    boolean existsByCoordinatesExcludingId(Double latitud, Double longitud, Integer zoneId);
    
    boolean existsByCodzona(Integer codzona);
    
    List<ZoneSummaryByLevel> getZonesSummaryBySecurityLevel();
    
    long getTotalZonesCount();
}
