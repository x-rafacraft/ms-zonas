package pe.com.practicar.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pe.com.practicar.business.dto.ZonesDto;
import pe.com.practicar.expose.schema.ZoneResponse;
import pe.com.practicar.repository.model.Zones;

@Mapper(componentModel = "spring")
public interface ZoneMapper {

    @Mapping(source = "id", target = "zoneCode")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "district", target = "district")
    @Mapping(source = "province", target = "province")
    @Mapping(source = "region", target = "region")
    @Mapping(source = "country", target = "country")
    @Mapping(source = "latitude", target = "latitude")
    @Mapping(source = "longitude", target = "longitude")
    @Mapping(source = "securityLevel", target = "securityLevel")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "active", target = "active")
    @Mapping(source = "createdBy", target = "createdBy")
    @Mapping(source = "updatedBy", target = "updatedBy")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    ZonesDto convertToZoneResponse(Zones zone);

    @Mapping(target = "codzona", source = "zoneCode")
    @Mapping(target = "nombre", source = "name")
    @Mapping(target = "distrito", source = "district")
    @Mapping(target = "provincia", source = "province")
    @Mapping(target = "region", source = "region")
    @Mapping(target = "pais", source = "country")
    @Mapping(target = "latitud", source = "latitude")
    @Mapping(target = "longitud", source = "longitude")
    @Mapping(target = "nivelSeguridad", source = "securityLevel")
    @Mapping(target = "descripcion", source = "description")
    @Mapping(target = "activo", source = "active")
    @Mapping(target = "usuarioCreacion", source = "createdBy")
    @Mapping(target = "usuarioActualizacion", source = "updatedBy")
    @Mapping(target = "fechaCreacion", source = "createdAt")
    @Mapping(target = "fechaActualizacion", source = "updatedAt")
    ZoneResponse zoneDtoToResponse(ZonesDto zonesDto);
}
