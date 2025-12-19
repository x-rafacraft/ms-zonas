package pe.com.practicar.repository.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import pe.com.practicar.repository.ZonesJdbcRepository;
import pe.com.practicar.repository.model.Zones;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class ZonesJdbcRepositoryImpl implements ZonesJdbcRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Value("${spring.jpa.properties.hibernate.default_schema:public}")
    private String schema;

    @Override
    public Mono<List<Zones>> getZonesPaginated(Integer currentPage, Integer pageSize) {
        return Mono.fromCallable(() -> {
            MapSqlParameterSource parameters = new MapSqlParameterSource();
            StringBuilder queryBuilder = new StringBuilder();

            queryBuilder.append("SELECT ")
                    .append("z.codzona AS id, ")
                    .append("z.nombre AS name, ")
                    .append("z.distrito AS district, ")
                    .append("z.provincia AS province, ")
                    .append("z.region AS region, ")
                    .append("z.pais AS country, ")
                    .append("z.latitud AS latitude, ")
                    .append("z.longitud AS longitude, ")
                    .append("z.nivelSeguridad AS securityLevel, ")
                    .append("z.descripcion AS description, ")
                    .append("z.activo AS active, ")
                    .append("z.usuarioCreacion AS createdBy, ")
                    .append("z.usuarioActualizacion AS updatedBy, ")
                    .append("z.fechaCreacion AS createdAt, ")
                    .append("z.fechaCreacion AS updatedAt ")
                    .append("FROM ")
                    .append(schema)
                    .append(".zonas z")
                    .append("ORDER BY z.nombre ");

                    int safePaginaActual = (currentPage != null && currentPage >= 1) ? currentPage : 1;
                    int safeTamanioPagina = (pageSize != null && pageSize > 0) ? pageSize : 10;

                    int boundedTamanioPagina = Math.min(safeTamanioPagina, 1000);

                    int offset;

                    try {
                        offset = Math.multiplyExact(safePaginaActual - 1, boundedTamanioPagina);
                    } catch (ArithmeticException ex) {
                        // Log y fallback a offset = 0 para seguridad
                        log.warn("Pagination offset arithmetic overflow/underflow for paginaActual: {}, "
                                        + "tamanioPagina: {}. Defaulting offset to 0.",
                                currentPage, pageSize);
                        offset = 0;
                    }

                    parameters.addValue("offset", offset);
                    parameters.addValue("tamanioPagina", boundedTamanioPagina);
                    queryBuilder.append("OFFSET :offset ROWS FETCH NEXT :tamanioPagina ROWS ONLY");

                    String query = queryBuilder.toString();

                    return namedParameterJdbcTemplate.query(query, parameters,
                            BeanPropertyRowMapper.newInstance(Zones.class));
                })
                .subscribeOn(Schedulers.boundedElastic());
    }
}
