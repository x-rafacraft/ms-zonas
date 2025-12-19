package pe.com.practicar.business.dto;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor
@Setter
@ToString
public class ZonesDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer zoneCode;

    private String names;

    private String districts;

    private String provinces;

    private String regions;

    private String countrys;

    private Double latitudes;

    private Double longitudes;

    private Integer securityLevels;

    private String descriptions;

    private Boolean actives;

    private String createdBys;

    private String updatedBys;

    private LocalDateTime createdAts;

    private LocalDateTime updatedAts;
}
