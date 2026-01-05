package pe.com.practicar.business.dto;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor
@Setter
@ToString
public class ZoneSummaryDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<ZoneSummaryByLevelDto> resumenPorNivel;
    
    private Long totalZonas;
}
