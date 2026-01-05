package pe.com.practicar.expose.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor
@Setter
@ToString
public class ZoneSummaryResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<ZoneSummaryByLevelResponse> resumenPorNivel;
    
    private Long totalZonas;

    @JsonProperty("resumenPorNivel")
    public List<ZoneSummaryByLevelResponse> getResumenPorNivel() { return resumenPorNivel; }

    public void setResumenPorNivel(List<ZoneSummaryByLevelResponse> resumenPorNivel) { 
        this.resumenPorNivel = resumenPorNivel; 
    }

    @JsonProperty("totalZonas")
    public Long getTotalZonas() { return totalZonas; }

    public void setTotalZonas(Long totalZonas) { this.totalZonas = totalZonas; }
}
