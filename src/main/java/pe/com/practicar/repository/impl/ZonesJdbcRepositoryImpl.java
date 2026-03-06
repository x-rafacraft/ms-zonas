package pe.com.practicar.repository.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import pe.com.practicar.expose.schema.ZoneDatosCreateRequest;
import pe.com.practicar.expose.schema.ZoneDatosUpdateRequest;
import pe.com.practicar.repository.ZonesJdbcRepository;
import pe.com.practicar.repository.model.ZoneSummaryByLevel;
import pe.com.practicar.repository.model.Zones;
import pe.com.practicar.util.Constants;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
@RequiredArgsConstructor
public class ZonesJdbcRepositoryImpl implements ZonesJdbcRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Value("${app.database.schema:public}")
    private String schema;

    private String buildSelectZonesQuery() {
        return """
                SELECT
                    z.codzona              AS id,
                    z.nombre               AS name,
                    z.distrito             AS district,
                    z.provincia            AS province,
                    z.region               AS region,
                    z.pais                 AS country,
                    z.latitud              AS latitude,
                    z.longitud             AS longitude,
                    z.nivelSeguridad       AS securityLevel,
                    z.descripcion          AS description,
                    z.activo               AS active,
                    z.usuarioCreacion      AS createdBy,
                    z.usuarioActualizacion AS updatedBy,
                    z.fechaCreacion        AS createdAt,
                    z.fechaActualizacion   AS updatedAt
                FROM %s.zonas z
                """.formatted(schema);
    }

    /**
     * Calcula el offset para paginación de forma segura
     */
    private int calculateOffset(Integer currentPage, Integer pageSize) {
        int safePage = (currentPage != null && currentPage >= 1) ? currentPage : 1;
        int safeSize = (pageSize != null && pageSize > 0) ? Math.min(pageSize, 1000) : 10;
        
        try {
            return Math.multiplyExact(safePage - 1, safeSize);
        } catch (ArithmeticException ex) {
            return 0;
        }
    }

    /**
     * Aplica paginación a la query
     */
    private void applyPagination(StringBuilder query, MapSqlParameterSource parameters, 
                                  Integer currentPage, Integer pageSize) {
        int safeSize = (pageSize != null && pageSize > 0) ? Math.min(pageSize, 1000) : 10;
        int offset = calculateOffset(currentPage, pageSize);
        
        parameters.addValue("offset", offset);
        parameters.addValue("tamanioPagina", safeSize);
        query.append("OFFSET :offset ROWS FETCH NEXT :tamanioPagina ROWS ONLY");
    }

    @Override
    public List<Zones> getZonesPaginated(Integer currentPage, Integer pageSize) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        StringBuilder queryBuilder = new StringBuilder();

        queryBuilder.append(buildSelectZonesQuery())
                .append("ORDER BY z.nombre ");

        applyPagination(queryBuilder, parameters, currentPage, pageSize);

        return namedParameterJdbcTemplate.query(queryBuilder.toString(), parameters,
                BeanPropertyRowMapper.newInstance(Zones.class));
    }

    @Override
    public Optional<Zones> updateZone(Integer zoneCode, ZoneDatosUpdateRequest updateRequest) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("codzona", zoneCode);

        StringBuilder updateQuery = buildUpdateQuery(updateRequest, parameters);
        namedParameterJdbcTemplate.update(updateQuery.toString(), parameters);

        return getZoneById(zoneCode);
    }


    private StringBuilder buildUpdateQuery(ZoneDatosUpdateRequest updateRequest, MapSqlParameterSource parameters) {
        StringBuilder updateQuery = new StringBuilder();
        updateQuery.append("UPDATE ").append(schema).append(".zonas SET ");
        
        boolean first = true;
        first = addFieldIfNotNull(updateQuery, parameters, Constants.FIELD_NOMBRE, updateRequest.getNombre(), first);
        first = addFieldIfNotNull(updateQuery, parameters, Constants.FIELD_LATITUD, updateRequest.getLatitud(), first);
        first = addFieldIfNotNull(updateQuery, parameters, Constants.FIELD_LONGITUD, updateRequest.getLongitud(), first);
        first = addFieldIfNotNull(updateQuery, parameters, Constants.FIELD_NIVEL_SEGURIDAD, updateRequest.getNivelSeguridad(), first);
        first = addFieldIfNotNull(updateQuery, parameters, Constants.FIELD_DESCRIPCION, updateRequest.getDescripcion(), first);
        first = addFieldIfNotNull(updateQuery, parameters, "activo", updateRequest.getActivo(), first);
        first = addFieldIfNotNull(updateQuery, parameters, "usuarioActualizacion", updateRequest.getUsuarioActualizacion(), first);
        
        if (!first) {
            updateQuery.append(", ");
        }
        updateQuery.append("fechaActualizacion = GETDATE() ");
        updateQuery.append("WHERE codzona = :codzona");
        
        return updateQuery;
    }

    private boolean addFieldIfNotNull(StringBuilder query, MapSqlParameterSource parameters, 
                                      String fieldName, Object value, boolean isFirst) {
        if (value != null) {
            if (!isFirst) {
                query.append(", ");
            }
            query.append(fieldName).append(" = :").append(fieldName);
            parameters.addValue(fieldName, value);
            return false;
        }
        return isFirst;
    }

    @Override
    public Zones createZone(ZoneDatosCreateRequest createRequest) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        
        // Activar IDENTITY_INSERT para permitir inserción manual del codzona
        String setIdentityOn = "SET IDENTITY_INSERT " + schema + ".zonas ON";
        namedParameterJdbcTemplate.getJdbcTemplate().execute(setIdentityOn);
        try {
            String insertQuery = """
                    INSERT INTO %s.zonas (
                        codzona, nombre, distrito, provincia, region, pais,
                        latitud, longitud, nivelSeguridad, descripcion,
                        activo, usuarioCreacion, fechaCreacion
                    ) VALUES (
                        :codzona, :nombre, :distrito, :provincia, :region, :pais,
                        :latitud, :longitud, :nivelSeguridad, :descripcion,
                        1, :usuarioCreacion, GETDATE()
                    )
                    """.formatted(schema);

            parameters.addValue("codzona", createRequest.getCodzona());
            parameters.addValue("nombre", createRequest.getNombre());
            parameters.addValue("distrito", createRequest.getDistrito());
            parameters.addValue("provincia", createRequest.getProvincia());
            parameters.addValue("region", createRequest.getRegion());
            parameters.addValue("pais", createRequest.getPais());
            parameters.addValue(Constants.FIELD_LATITUD, createRequest.getLatitud());
            parameters.addValue(Constants.FIELD_LONGITUD, createRequest.getLongitud());
            parameters.addValue("nivelSeguridad", createRequest.getNivelSeguridad());
            parameters.addValue("descripcion", createRequest.getDescripcion());
            parameters.addValue("usuarioCreacion", createRequest.getUsuarioCreacion());

            namedParameterJdbcTemplate.update(insertQuery, parameters);
        } finally {
            // Desactivar IDENTITY_INSERT siempre, incluso si el INSERT falla
            String setIdentityOff = "SET IDENTITY_INSERT " + schema + ".zonas OFF";
            namedParameterJdbcTemplate.getJdbcTemplate().execute(setIdentityOff);
        }

        return getZoneById(createRequest.getCodzona())
                .orElseThrow(() -> new IllegalStateException(
                        "La zona fue creada pero no se pudo recuperar con código: " + createRequest.getCodzona()));
    }

    @Override
    public List<Zones> getZonesWithFilters(Integer currentPage, Integer pageSize, String province, String district, Integer securityLevel) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        StringBuilder queryBuilder = new StringBuilder();

        queryBuilder.append(buildSelectZonesQuery())
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

        applyPagination(queryBuilder, parameters, currentPage, pageSize);

        return namedParameterJdbcTemplate.query(queryBuilder.toString(), parameters,
                BeanPropertyRowMapper.newInstance(Zones.class));
    }

    @Override
    public Optional<Zones> getZoneById(Integer zoneCode) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("codzona", zoneCode);

        String query = buildSelectZonesQuery() + "WHERE z.codzona = :codzona";

        List<Zones> result = namedParameterJdbcTemplate.query(
                query,
                parameters,
                BeanPropertyRowMapper.newInstance(Zones.class)
        );

        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public Optional<Zones> replaceZone(Integer zoneCode, ZoneDatosCreateRequest replaceRequest) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("codzona", zoneCode);

        String updateQuery = """
                UPDATE %s.zonas SET
                    nombre               = :nombre,
                    distrito             = :distrito,
                    provincia            = :provincia,
                    region               = :region,
                    pais                 = :pais,
                    latitud              = :latitud,
                    longitud             = :longitud,
                    nivelSeguridad       = :nivelSeguridad,
                    descripcion          = :descripcion,
                    usuarioActualizacion = :usuarioCreacion,
                    fechaActualizacion   = GETDATE()
                WHERE codzona = :codzona
                """.formatted(schema);

        parameters.addValue(Constants.FIELD_NOMBRE, replaceRequest.getNombre());
        parameters.addValue("distrito", replaceRequest.getDistrito());
        parameters.addValue("provincia", replaceRequest.getProvincia());
        parameters.addValue("region", replaceRequest.getRegion());
        parameters.addValue("pais", replaceRequest.getPais());
        parameters.addValue(Constants.FIELD_LATITUD, replaceRequest.getLatitud());
        parameters.addValue(Constants.FIELD_LONGITUD, replaceRequest.getLongitud());
        parameters.addValue(Constants.FIELD_NIVEL_SEGURIDAD, replaceRequest.getNivelSeguridad());
        parameters.addValue(Constants.FIELD_DESCRIPCION, replaceRequest.getDescripcion());
        parameters.addValue("usuarioCreacion", replaceRequest.getUsuarioCreacion());

        namedParameterJdbcTemplate.update(updateQuery, parameters);

        return getZoneById(zoneCode);
    }

    @Override
    public int deleteZone(Integer zoneCode) {
        String query = """
                DELETE FROM %s.zonas
                WHERE codzona = :codzona
                """.formatted(schema);
        return namedParameterJdbcTemplate.update(query,
                new MapSqlParameterSource("codzona", zoneCode));
    }

    @Override
    public boolean existsByNombre(String nombre) {
        String query = """
                SELECT COUNT(*)
                FROM %s.zonas
                WHERE LOWER(nombre) = LOWER(:nombre)
                """.formatted(schema);
        Integer count = namedParameterJdbcTemplate.queryForObject(query,
                new MapSqlParameterSource(Constants.FIELD_NOMBRE, nombre), Integer.class);
        return count != null && count > 0;
    }

    @Override
    public boolean existsByCoordinates(Double latitud, Double longitud) {
        String query = """
                SELECT COUNT(*)
                FROM %s.zonas
                WHERE latitud = :latitud
                  AND longitud = :longitud
                """.formatted(schema);
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue(Constants.FIELD_LATITUD, latitud)
                .addValue(Constants.FIELD_LONGITUD, longitud);
        Integer count = namedParameterJdbcTemplate.queryForObject(query, parameters, Integer.class);
        return count != null && count > 0;
    }

    @Override
    public boolean existsByNombreExcludingId(String nombre, Integer zoneId) {
        String query = """
                SELECT COUNT(*)
                FROM %s.zonas
                WHERE LOWER(nombre) = LOWER(:nombre)
                  AND codzona != :codzona
                """.formatted(schema);
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue(Constants.FIELD_NOMBRE, nombre)
                .addValue("codzona", zoneId);
        Integer count = namedParameterJdbcTemplate.queryForObject(query, parameters, Integer.class);
        return count != null && count > 0;
    }

    @Override
    public boolean existsByCoordinatesExcludingId(Double latitud, Double longitud, Integer zoneId) {
        String query = """
                SELECT COUNT(*)
                FROM %s.zonas
                WHERE latitud = :latitud
                  AND longitud = :longitud
                  AND codzona != :codzona
                """.formatted(schema);
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue(Constants.FIELD_LATITUD, latitud)
                .addValue(Constants.FIELD_LONGITUD, longitud)
                .addValue("codzona", zoneId);
        Integer count = namedParameterJdbcTemplate.queryForObject(query, parameters, Integer.class);
        return count != null && count > 0;
    }

    @Override
    public boolean existsByCodzona(Integer codzona) {
        String query = """
                SELECT COUNT(*)
                FROM %s.zonas
                WHERE codzona = :codzona
                """.formatted(schema);
        Integer count = namedParameterJdbcTemplate.queryForObject(query,
                new MapSqlParameterSource("codzona", codzona), Integer.class);
        return count != null && count > 0;
    }

    @Override
    public List<ZoneSummaryByLevel> getZonesSummaryBySecurityLevel() {
        String query = """
                SELECT
                    z.nivelSeguridad AS securityLevel,
                    COUNT(*)         AS count
                FROM %s.zonas z
                GROUP BY z.nivelSeguridad
                ORDER BY z.nivelSeguridad
                """.formatted(schema);
        return namedParameterJdbcTemplate.query(query, new MapSqlParameterSource(),
                BeanPropertyRowMapper.newInstance(ZoneSummaryByLevel.class));
    }

    @Override
    public long getTotalZonesCount() {
        String query = """
                SELECT COUNT(*)
                FROM %s.zonas
                """.formatted(schema);
        Long count = namedParameterJdbcTemplate.queryForObject(query,
                new MapSqlParameterSource(), Long.class);
        return count != null ? count : 0L;
    }
}
