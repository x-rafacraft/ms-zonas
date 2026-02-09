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

    @Value("${spring.jpa.properties.hibernate.default_schema:public}")
    private String schema;

    /**
     * Construye el SELECT base para consultas de zonas
     */
    private String buildSelectZonesQuery() {
        return "SELECT " +
                "z.codzona AS id, " +
                "z.nombre AS name, " +
                "z.distrito AS district, " +
                "z.provincia AS province, " +
                "z.region AS region, " +
                "z.pais AS country, " +
                "z.latitud AS latitude, " +
                "z.longitud AS longitude, " +
                "z.nivelSeguridad AS securityLevel, " +
                "z.descripcion AS description, " +
                "z.activo AS active, " +
                "z.usuarioCreacion AS createdBy, " +
                "z.usuarioActualizacion AS updatedBy, " +
                "z.fechaCreacion AS createdAt, " +
                "z.fechaActualizacion AS updatedAt " +
                "FROM " + schema + ".zonas z ";
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

        validateZoneExists(zoneCode, parameters);
        
        StringBuilder updateQuery = buildUpdateQuery(updateRequest, parameters);
        namedParameterJdbcTemplate.update(updateQuery.toString(), parameters);

        return getZoneById(zoneCode);
    }

    private void validateZoneExists(Integer zoneCode, MapSqlParameterSource parameters) {
        StringBuilder checkQueryBuilder = new StringBuilder();
        checkQueryBuilder.append("SELECT COUNT(*) FROM ")
                .append(schema)
                .append(".zonas WHERE codzona = :codzona");
        
        Integer count = namedParameterJdbcTemplate.queryForObject(
                checkQueryBuilder.toString(), parameters, Integer.class);
        
        if (count == null || count == 0) {
            throw new RuntimeException("Zona con código " + zoneCode + " no encontrada");
        }
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
            parameters.addValue(Constants.FIELD_LATITUD, createRequest.getLatitud());
            parameters.addValue(Constants.FIELD_LONGITUD, createRequest.getLongitud());
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
                    .append(Constants.FIELD_NOMBRE).append(" = :").append(Constants.FIELD_NOMBRE).append(", ")
                    .append("distrito = :distrito, ")
                    .append("provincia = :provincia, ")
                    .append("region = :region, ")
                    .append("pais = :pais, ")
                    .append(Constants.FIELD_LATITUD).append(" = :").append(Constants.FIELD_LATITUD).append(", ")
                    .append(Constants.FIELD_LONGITUD).append(" = :").append(Constants.FIELD_LONGITUD).append(", ")
                    .append(Constants.FIELD_NIVEL_SEGURIDAD).append(" = :").append(Constants.FIELD_NIVEL_SEGURIDAD).append(", ")
                    .append(Constants.FIELD_DESCRIPCION).append(" = :").append(Constants.FIELD_DESCRIPCION).append(", ")
                    .append("usuarioActualizacion = :usuarioCreacion, ")
                    .append("fechaActualizacion = GETDATE() ")
                    .append("WHERE codzona = :codzona");

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

        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public int deleteZone(Integer zoneCode) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("codzona", zoneCode);

        StringBuilder deleteQuery = new StringBuilder();
        deleteQuery.append("DELETE FROM ")
                .append(schema)
                .append(".zonas ")
                .append("WHERE codzona = :codzona");

        return namedParameterJdbcTemplate.update(deleteQuery.toString(), parameters);
    }

    @Override
    public boolean existsByNombre(String nombre) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue(Constants.FIELD_NOMBRE, nombre);

        StringBuilder query = new StringBuilder();
        query.append("SELECT COUNT(*) FROM ")
                .append(schema)
                .append(".zonas ")
                .append("WHERE LOWER(").append(Constants.FIELD_NOMBRE).append(") = LOWER(:").append(Constants.FIELD_NOMBRE).append(")");

        Integer count = namedParameterJdbcTemplate.queryForObject(
                query.toString(), parameters, Integer.class);

        return count != null && count > 0;
    }

    @Override
    public boolean existsByCoordinates(Double latitud, Double longitud) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue(Constants.FIELD_LATITUD, latitud);
        parameters.addValue(Constants.FIELD_LONGITUD, longitud);

        StringBuilder query = new StringBuilder();
        query.append("SELECT COUNT(*) FROM ")
                .append(schema)
                .append(".zonas ")
                .append("WHERE latitud = :latitud AND longitud = :longitud");

        Integer count = namedParameterJdbcTemplate.queryForObject(
                query.toString(), parameters, Integer.class);

        return count != null && count > 0;
    }

    @Override
    public boolean existsByNombreExcludingId(String nombre, Integer zoneId) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue(Constants.FIELD_NOMBRE, nombre);
        parameters.addValue("codzona", zoneId);

        StringBuilder query = new StringBuilder();
        query.append("SELECT COUNT(*) FROM ")
                .append(schema)
                .append(".zonas ")
                .append("WHERE LOWER(").append(Constants.FIELD_NOMBRE).append(") = LOWER(:").append(Constants.FIELD_NOMBRE).append(") AND codzona != :codzona");

        Integer count = namedParameterJdbcTemplate.queryForObject(
                query.toString(), parameters, Integer.class);

        return count != null && count > 0;
    }

    @Override
    public boolean existsByCoordinatesExcludingId(Double latitud, Double longitud, Integer zoneId) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue(Constants.FIELD_LATITUD, latitud);
        parameters.addValue(Constants.FIELD_LONGITUD, longitud);
        parameters.addValue("codzona", zoneId);

        StringBuilder query = new StringBuilder();
        query.append("SELECT COUNT(*) FROM ")
                .append(schema)
                .append(".zonas ")
                .append("WHERE latitud = :latitud AND longitud = :longitud AND codzona != :codzona");

        Integer count = namedParameterJdbcTemplate.queryForObject(
                query.toString(), parameters, Integer.class);

        return count != null && count > 0;
    }

    @Override
    public boolean existsByCodzona(Integer codzona) {
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
    }

    @Override
    public List<ZoneSummaryByLevel> getZonesSummaryBySecurityLevel() {
        StringBuilder query = new StringBuilder();
        query.append("SELECT ")
                .append("z.nivelSeguridad AS securityLevel, ")
                .append("COUNT(*) AS count ")
                .append("FROM ")
                .append(schema)
                .append(".zonas z ")
                .append("GROUP BY z.nivelSeguridad ")
                .append("ORDER BY z.nivelSeguridad");

        return namedParameterJdbcTemplate.query(
                query.toString(),
                new MapSqlParameterSource(),
                BeanPropertyRowMapper.newInstance(ZoneSummaryByLevel.class)
        );
    }

    @Override
    public long getTotalZonesCount() {
        StringBuilder query = new StringBuilder();
        query.append("SELECT COUNT(*) FROM ")
                .append(schema)
                .append(".zonas");

        Integer count = namedParameterJdbcTemplate.queryForObject(
                query.toString(),
                new MapSqlParameterSource(),
                Integer.class
        );

        return count != null ? count.longValue() : 0L;
    }
}
