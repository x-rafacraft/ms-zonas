package pe.com.practicar.repository;

import pe.com.practicar.repository.model.Zones;
import reactor.core.publisher.Mono;

import java.util.List;

@FunctionalInterface
public interface ZonesJdbcRepository {

    Mono<List<Zones>> getZonesPaginated(Integer currentPage, Integer pageSize);
}
