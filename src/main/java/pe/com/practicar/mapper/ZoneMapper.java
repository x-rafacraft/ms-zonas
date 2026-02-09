package pe.com.practicar.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pe.com.practicar.business.dto.ZonesDto;
import pe.com.practicar.repository.model.Zones;

@Mapper(componentModel = "spring")
public interface ZoneMapper {

    @Mapping(source = "id", target = "zoneCode")
    @Mapping(source = "name", target = "names")
    @Mapping(source = "district", target = "districts")
    @Mapping(source = "province", target = "provinces")
    @Mapping(source = "region", target = "regions")
    @Mapping(source = "country", target = "countrys")
    @Mapping(source = "latitude", target = "latitudes")
    @Mapping(source = "longitude", target = "longitudes")
    @Mapping(source = "securityLevel", target = "securityLevels")
    @Mapping(source = "description", target = "descriptions")
    @Mapping(source = "active", target = "actives")
    @Mapping(source = "createdBy", target = "createdBys")
    @Mapping(source = "updatedBy", target = "updatedBys")
    @Mapping(source = "createdAt", target = "createdAts")
    @Mapping(source = "updatedAt", target = "updatedAts")
    ZonesDto convertToZoneResponse(Zones zone);
}
