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
            
            if (updateRequest.getNombre() != null) {
                updateQuery.append("nombre = :nombre");
                parameters.addValue("nombre", updateRequest.getNombre());
                first = false;
            }
            
            if (updateRequest.getLatitud() != null) {
                if (!first) updateQuery.append(", ");
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
            
            // Activar IDENTITY_INSERT para permitir inserción manual del codzona
            String setIdentityOn = "SET IDENTITY_INSERT " + schema + ".zonas ON";
            namedParameterJdbcTemplate.getJdbcTemplate().execute(setIdentityOn);
            
            StringBuilder insertQuery = new StringBuilder();
            insertQuery.append("INSERT INTO ").append(schema).append(".zonas ")
                    .append("(codzona, nombre, distrito, provincia, region, pais, latitud, longitud, ")
                    .append("nivelSeguridad, descripcion, activo, usuarioCreacion, fechaCreacion) ")
                    .append("VALUES ")
                    .append("(:codzona, :nombre, :distrito, :provincia, :region, :pais, :latitud, :longitud, ")
                    .append(":nivelSeguridad, :descripcion, 1, :usuarioCreacion, GETDATE())");
            
            parameters.addValue("codzona", createRequest.getCodzona());
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
            
            namedParameterJdbcTemplate.update(insertQuery.toString(), parameters);
            
            // Desactivar IDENTITY_INSERT
            String setIdentityOff = "SET IDENTITY_INSERT " + schema + ".zonas OFF";
            namedParameterJdbcTemplate.getJdbcTemplate().execute(setIdentityOff);
            
            // Consultar la zona creada
            MapSqlParameterSource selectParams = new MapSqlParameterSource();
            selectParams.addValue("codzona", createRequest.getCodzona());
            
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

    @Override
    public Mono<List<Zones>> getZonesWithFilters(Integer currentPage, Integer pageSize, String province, String district, Integer securityLevel) {
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
                    .append(".zonas z ")
                    .append("WHERE 1=1 ");

            if (province != null && !province.isBlank()) {
                queryBuilder.append("AND z.provincia LIKE :province ");
                parameters.addValue("province", "%" + province + "%");
            }

            if (district != null && !district.isBlank()) {
                queryBuilder.append("AND z.distrito LIKE :district ");
                parameters.addValue("district", "%" + district + "%");
            }

            if (securityLevel != null) {
                queryBuilder.append("AND z.nivelSeguridad = :securityLevel ");
                parameters.addValue("securityLevel", securityLevel);
            }

            queryBuilder.append("ORDER BY z.nombre ");

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
    public Mono<Zones> getZoneById(Integer zoneCode) {
        return Mono.fromCallable(() -> {
            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("codzona", zoneCode);

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
                    .append(".zonas z ")
                    .append("WHERE z.codzona = :codzona");

            List<Zones> result = namedParameterJdbcTemplate.query(
                    queryBuilder.toString(),
                    parameters,
                    BeanPropertyRowMapper.newInstance(Zones.class)
            );

            return result.isEmpty() ? null : result.get(0);
        })
        .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Zones> replaceZone(Integer zoneCode, ZoneDatosCreateRequest replaceRequest) {
        return Mono.fromCallable(() -> {
            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("codzona", zoneCode);

            // Verificar que existe
            StringBuilder checkQueryBuilder = new StringBuilder();
            checkQueryBuilder.append("SELECT COUNT(*) FROM ")
                    .append(schema)
                    .append(".zonas WHERE codzona = :codzona");

            Integer count = namedParameterJdbcTemplate.queryForObject(
                    checkQueryBuilder.toString(), parameters, Integer.class);

            if (count == null || count == 0) {
                throw new RuntimeException("Zona con código " + zoneCode + " no encontrada");
            }

            // Realizar el reemplazo completo (PUT)
            StringBuilder updateQuery = new StringBuilder();
            updateQuery.append("UPDATE ").append(schema).append(".zonas SET ")
                    .append("nombre = :nombre, ")
                    .append("distrito = :distrito, ")
                    .append("provincia = :provincia, ")
                    .append("region = :region, ")
                    .append("pais = :pais, ")
                    .append("latitud = :latitud, ")
                    .append("longitud = :longitud, ")
                    .append("nivelSeguridad = :nivelSeguridad, ")
                    .append("descripcion = :descripcion, ")
                    .append("usuarioActualizacion = :usuarioCreacion, ")
                    .append("fechaActualizacion = GETDATE() ")
                    .append("WHERE codzona = :codzona");

            parameters.addValue("nombre", replaceRequest.getNombre());
            parameters.addValue("distrito", replaceRequest.getDistrito());
            parameters.addValue("provincia", replaceRequest.getProvincia());
            parameters.addValue("region", replaceRequest.getRegion());
            parameters.addValue("pais", replaceRequest.getPais());
            parameters.addValue("latitud", replaceRequest.getLatitud());
            parameters.addValue("longitud", replaceRequest.getLongitud());
            parameters.addValue("nivelSeguridad", replaceRequest.getNivelSeguridad());
            parameters.addValue("descripcion", replaceRequest.getDescripcion());
            parameters.addValue("usuarioCreacion", replaceRequest.getUsuarioCreacion());

            namedParameterJdbcTemplate.update(updateQuery.toString(), parameters);

            // Consultar la zona actualizada
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
    public Mono<Integer> deleteZone(Integer zoneCode) {
        return Mono.fromCallable(() -> {
            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("codzona", zoneCode);

            StringBuilder deleteQuery = new StringBuilder();
            deleteQuery.append("DELETE FROM ")
                    .append(schema)
                    .append(".zonas ")
                    .append("WHERE codzona = :codzona");

            int rowsAffected = namedParameterJdbcTemplate.update(deleteQuery.toString(), parameters);
            
            return rowsAffected;
        })
        .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Boolean> existsByNombre(String nombre) {
        return Mono.fromCallable(() -> {
            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("nombre", nombre);

            StringBuilder query = new StringBuilder();
            query.append("SELECT COUNT(*) FROM ")
                    .append(schema)
                    .append(".zonas ")
                    .append("WHERE LOWER(nombre) = LOWER(:nombre)");

            Integer count = namedParameterJdbcTemplate.queryForObject(
                    query.toString(), parameters, Integer.class);

            return count != null && count > 0;
        })
        .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Boolean> existsByCoordinates(Double latitud, Double longitud) {
        return Mono.fromCallable(() -> {
            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("latitud", latitud);
            parameters.addValue("longitud", longitud);

            StringBuilder query = new StringBuilder();
            query.append("SELECT COUNT(*) FROM ")
                    .append(schema)
                    .append(".zonas ")
                    .append("WHERE latitud = :latitud AND longitud = :longitud");

            Integer count = namedParameterJdbcTemplate.queryForObject(
                    query.toString(), parameters, Integer.class);

            return count != null && count > 0;
        })
        .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Boolean> existsByNombreExcludingId(String nombre, Integer zoneId) {
        return Mono.fromCallable(() -> {
            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("nombre", nombre);
            parameters.addValue("codzona", zoneId);

            StringBuilder query = new StringBuilder();
            query.append("SELECT COUNT(*) FROM ")
                    .append(schema)
                    .append(".zonas ")
                    .append("WHERE LOWER(nombre) = LOWER(:nombre) AND codzona != :codzona");

            Integer count = namedParameterJdbcTemplate.queryForObject(
                    query.toString(), parameters, Integer.class);

            return count != null && count > 0;
        })
        .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Boolean> existsByCoordinatesExcludingId(Double latitud, Double longitud, Integer zoneId) {
        return Mono.fromCallable(() -> {
            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("latitud", latitud);
            parameters.addValue("longitud", longitud);
            parameters.addValue("codzona", zoneId);

            StringBuilder query = new StringBuilder();
            query.append("SELECT COUNT(*) FROM ")
                    .append(schema)
                    .append(".zonas ")
                    .append("WHERE latitud = :latitud AND longitud = :longitud AND codzona != :codzona");

            Integer count = namedParameterJdbcTemplate.queryForObject(
                    query.toString(), parameters, Integer.class);

            return count != null && count > 0;
        })
        .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Boolean> existsByCodzona(Integer codzona) {
        return Mono.fromCallable(() -> {
            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("codzona", codzona);

            StringBuilder query = new StringBuilder();
            query.append("SELECT COUNT(*) FROM ")
                    .append(schema)
                    .append(".zonas ")
                    .append("WHERE codzona = :codzona");

            Integer count = namedParameterJdbcTemplate.queryForObject(
                    query.toString(), parameters, Integer.class);

            return count != null && count > 0;
        })
        .subscribeOn(Schedulers.boundedElastic());
    }
}
