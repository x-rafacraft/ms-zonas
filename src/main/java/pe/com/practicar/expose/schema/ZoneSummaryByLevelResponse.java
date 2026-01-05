package pe.com.practicar.expose.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor
@Setter
@ToString
public class ZoneSummaryByLevelResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer nivelSeguridad;
    
    private Long cantidad;

    @JsonProperty("nivelSeguridad")
    public Integer getNivelSeguridad() { return nivelSeguridad; }

    public void setNivelSeguridad(Integer nivelSeguridad) { this.nivelSeguridad = nivelSeguridad; }

    @JsonProperty("cantidad")
    public Long getCantidad() { return cantidad; }

    public void setCantidad(Long cantidad) { this.cantidad = cantidad; }
}
