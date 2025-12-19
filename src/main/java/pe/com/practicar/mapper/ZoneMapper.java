package pe.com.practicar.mapper;

import org.springframework.stereotype.Service;
import pe.com.practicar.business.dto.ZonesDto;
import pe.com.practicar.repository.model.Zones;

@Service
public class ZoneMapper {

   public ZonesDto convertToZoneResponse(Zones zone) {
       return ZonesDto.builder()
               .zoneCode(zone.getId())
               .names(zone.getName())
               .districts(zone.getDistrict())
               .provinces(zone.getProvince())
               .regions(zone.getRegion())
               .countrys(zone.getCountry())
               .latitudes(zone.getLatitude())
               .longitudes(zone.getLongitude())
               .securityLevels(zone.getSecurityLevel())
               .descriptions(zone.getDescription())
               .actives(zone.getActive())
               .createdBys(zone.getCreatedBy())
               .updatedBys(zone.getUpdatedBy())
               .createdAts(zone.getCreatedAt())
               .updatedAts(zone.getUpdatedAt())
               .build();
   }
}
