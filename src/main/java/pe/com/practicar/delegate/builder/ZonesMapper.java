package pe.com.practicar.delegate.builder;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import pe.com.practicar.business.dto.ZonesDto;
import pe.com.practicar.expose.schema.ZoneResponse;

@Mapper(componentModel = "spring")
public interface ZonesMapper {

    ZonesMapper INSTANCE = Mappers.getMapper(ZonesMapper.class);

    @Mapping(target = "nombre", source = "names")
    @Mapping(target = "distrito", source = "districts")
    @Mapping(target = "provincia", source = "provinces")
    @Mapping(target = "region", source = "regions")
    @Mapping(target = "pais", source = "countrys")
    @Mapping(target = "latitud", source = "latitudes")
    @Mapping(target = "longitud", source = "longitudes")
    @Mapping(target = "nivelSeguridad", source = "securityLevels")
    @Mapping(target = "descripcion", source = "descriptions")
    @Mapping(target = "activo", source = "actives")
    @Mapping(target = "usuarioCreacion", source = "createdBys")
    @Mapping(target = "usuarioActualizacion", source = "updatedBys")
    @Mapping(target = "fechaCreacion", source = "createdAts")
    @Mapping(target = "fechaActualizacion", source = "updatedAts")
    ZoneResponse zoneDtoToResponse(ZonesDto zonesDto);
}
