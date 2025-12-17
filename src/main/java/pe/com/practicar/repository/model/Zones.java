package pe.com.practicar.repository.model;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Zones implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer id;

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
