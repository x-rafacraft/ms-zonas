package pe.com.practicar.business.dto;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor
@Setter
@ToString
public class ZonesPaginatedDto {

    private List<ZonesDto> zones;
    private Integer currentPage;
    private Integer pageSize;
}
