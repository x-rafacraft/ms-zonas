package pe.com.practicar.expose.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.io.Serializable;

public class ZoneDatosUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private Double latitud;
    private Double longitud;
    private Integer nivelSeguridad;
    private String descripcion;
    private Boolean activo;
    private String usuarioActualizacion;

    @JsonProperty("latitud")
    public Double getLatitud() { return latitud; }

    public void setLatitud(Double latitud) { this.latitud = latitud; }

    @JsonProperty("longitud")
    public Double getLongitud() { return longitud; }

    public void setLongitud(Double longitud) { this.longitud = longitud; }

    @JsonProperty("nivelSeguridad")
    @Min(1)
    @Max(5)
    public Integer getNivelSeguridad() { return nivelSeguridad; }

    public void setNivelSeguridad(Integer nivelSeguridad) { this.nivelSeguridad = nivelSeguridad; }

    @JsonProperty("descripcion")
    public String getDescripcion() { return descripcion; }

    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    @JsonProperty("activo")
    public Boolean getActivo() { return activo; }

    public void setActivo(Boolean activo) { this.activo = activo; }

    @JsonProperty("usuarioActualizacion")
    public String getUsuarioActualizacion() { return usuarioActualizacion; }

    public void setUsuarioActualizacion(String usuarioActualizacion) { 
        this.usuarioActualizacion = usuarioActualizacion; 
    }
}
