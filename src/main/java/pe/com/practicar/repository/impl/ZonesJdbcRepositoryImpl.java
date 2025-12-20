package pe.com.practicar.repository.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import pe.com.practicar.expose.schema.ZoneDatosCreateRequest;
import pe.com.practicar.expose.schema.ZoneDatosUpdateRequest;
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
                    .append("z.fechaActualizacion AS updatedAt ")
                    .append("FROM ")
                    .append(schema)
                    .append(".zonas z")
                    .append(" ORDER BY z.nombre ");

                    int safePaginaActual = (currentPage != null && currentPage >= 1) ? currentPage : 1;
                    int safeTamanioPagina = (pageSize != null && pageSize > 0) ? pageSize : 10;

                    int boundedTamanioPagina = Math.min(safeTamanioPagina, 1000);

                    int offset;

                    try {
                        offset = Math.multiplyExact(safePaginaActual - 1, boundedTamanioPagina);
                    } catch (ArithmeticException ex) {
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

    @Override
    public Mono<Zones> updateZone(Integer zoneCode, ZoneDatosUpdateRequest updateRequest) {
        return Mono.fromCallable(() -> {
            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("codzona", zoneCode);

            StringBuilder checkQueryBuilder = new StringBuilder();
            checkQueryBuilder.append("SELECT COUNT(*) FROM ")
                    .append(schema)
                    .append(".zonas WHERE codzona = :codzona");
            
            Integer count = namedParameterJdbcTemplate.queryForObject(
                    checkQueryBuilder.toString(), parameters, Integer.class);
            
            if (count == null || count == 0) {
                throw new RuntimeException("Zona con código " + zoneCode + " no encontrada");
            }
            
            StringBuilder updateQuery = new StringBuilder();
            updateQuery.append("UPDATE ").append(schema).append(".zonas SET ");
            
            boolean first = true;
            
            if (updateRequest.getLatitud() != null) {
                updateQuery.append("latitud = :latitud");
                parameters.addValue("latitud", updateRequest.getLatitud());
                first = false;
            }
            
            if (updateRequest.getLongitud() != null) {
                if (!first) updateQuery.append(", ");
                updateQuery.append("longitud = :longitud");
                parameters.addValue("longitud", updateRequest.getLongitud());
                first = false;
            }
            
            if (updateRequest.getNivelSeguridad() != null) {
                if (!first) updateQuery.append(", ");
                updateQuery.append("nivelSeguridad = :nivelSeguridad");
                parameters.addValue("nivelSeguridad", updateRequest.getNivelSeguridad());
                first = false;
            }
            
            if (updateRequest.getDescripcion() != null) {
                if (!first) updateQuery.append(", ");
                updateQuery.append("descripcion = :descripcion");
                parameters.addValue("descripcion", updateRequest.getDescripcion());
                first = false;
            }
            
            if (updateRequest.getActivo() != null) {
                if (!first) updateQuery.append(", ");
                updateQuery.append("activo = :activo");
                parameters.addValue("activo", updateRequest.getActivo());
                first = false;
            }
            
            if (updateRequest.getUsuarioActualizacion() != null) {
                if (!first) updateQuery.append(", ");
                updateQuery.append("usuarioActualizacion = :usuarioActualizacion");
                parameters.addValue("usuarioActualizacion", updateRequest.getUsuarioActualizacion());
                first = false;
            }
            
            if (!first) {
                updateQuery.append(", ");
            }
            updateQuery.append("fechaActualizacion = GETDATE() ");
            updateQuery.append("WHERE codzona = :codzona");
            
            namedParameterJdbcTemplate.update(updateQuery.toString(), parameters);

            StringBuilder selectQueryBuilder = new StringBuilder();
            selectQueryBuilder.append("SELECT ")
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
                    .append("z.fechaActualizacion AS updatedAt ")
                    .append("FROM ")
                    .append(schema)
                    .append(".zonas z ")
                    .append("WHERE z.codzona = :codzona");
            
            List<Zones> result = namedParameterJdbcTemplate.query(
                    selectQueryBuilder.toString(),
                    parameters,
                    BeanPropertyRowMapper.newInstance(Zones.class)
            );
            
            return result.isEmpty() ? null : result.get(0);
        })
        .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Zones> createZone(ZoneDatosCreateRequest createRequest) {
        return Mono.fromCallable(() -> {
            MapSqlParameterSource parameters = new MapSqlParameterSource();
            
            StringBuilder insertQuery = new StringBuilder();
            insertQuery.append("INSERT INTO ").append(schema).append(".zonas ")
                    .append("(nombre, distrito, provincia, region, pais, latitud, longitud, ")
                    .append("nivelSeguridad, descripcion, activo, usuarioCreacion, fechaCreacion) ")
                    .append("VALUES ")
                    .append("(:nombre, :distrito, :provincia, :region, :pais, :latitud, :longitud, ")
                    .append(":nivelSeguridad, :descripcion, 1, :usuarioCreacion, GETDATE())");
            
            parameters.addValue("nombre", createRequest.getNombre());
            parameters.addValue("distrito", createRequest.getDistrito());
            parameters.addValue("provincia", createRequest.getProvincia());
            parameters.addValue("region", createRequest.getRegion());
            parameters.addValue("pais", createRequest.getPais());
            parameters.addValue("latitud", createRequest.getLatitud());
            parameters.addValue("longitud", createRequest.getLongitud());
            parameters.addValue("nivelSeguridad", createRequest.getNivelSeguridad());
            parameters.addValue("descripcion", createRequest.getDescripcion());
            parameters.addValue("usuarioCreacion", createRequest.getUsuarioCreacion());
            
            KeyHolder keyHolder = new GeneratedKeyHolder();
            namedParameterJdbcTemplate.update(insertQuery.toString(), parameters, keyHolder, new String[]{"codzona"});
            
            Number key = keyHolder.getKey();
            Integer zoneId = key != null ? key.intValue() : null;
            
            if (zoneId == null) {
                throw new RuntimeException("No se pudo obtener el ID de la zona creada");
            }
            
            // Consultar la zona creada
            MapSqlParameterSource selectParams = new MapSqlParameterSource();
            selectParams.addValue("codzona", zoneId);
            
            StringBuilder selectQueryBuilder = new StringBuilder();
            selectQueryBuilder.append("SELECT ")
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
                    .append("z.fechaActualizacion AS updatedAt ")
                    .append("FROM ")
                    .append(schema)
                    .append(".zonas z ")
                    .append("WHERE z.codzona = :codzona");
            
            List<Zones> result = namedParameterJdbcTemplate.query(
                    selectQueryBuilder.toString(),
                    selectParams,
                    BeanPropertyRowMapper.newInstance(Zones.class)
            );
            
            return result.isEmpty() ? null : result.get(0);
        })
        .subscribeOn(Schedulers.boundedElastic());
    }
}
