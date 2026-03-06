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

    private String name;

    private String district;

    private String province;

    private String region;

    private String country;

    private Double latitude;

    private Double longitude;

    private Integer securityLevel;

    private String description;

    private Boolean active;

    private String createdBy;

    private String updatedBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
