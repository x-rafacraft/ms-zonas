package pe.com.practicar.business.dto;

import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor
@Setter
@ToString
public class ZoneSummaryByLevelDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer nivelSeguridad;
    
    private Long cantidad;
}
