package pe.com.practicar.expose.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

import java.io.Serializable;

public class ZoneDatosCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "El código de zona es obligatorio")
    private Integer codzona;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    
    @NotBlank(message = "El distrito es obligatorio")
    private String distrito;
    
    @NotBlank(message = "La provincia es obligatoria")
    private String provincia;
    
    @NotBlank(message = "La región es obligatoria")
    private String region;
    
    @NotBlank(message = "El país es obligatorio")
    private String pais;
    
    @NotNull(message = "La latitud es obligatoria")
    private Double latitud;
    
    @NotNull(message = "La longitud es obligatoria")
    private Double longitud;
    
    @NotNull(message = "El nivel de seguridad es obligatorio")
    @Min(value = 1, message = "El nivel de seguridad debe ser entre 1 y 5")
    @Max(value = 5, message = "El nivel de seguridad debe ser entre 1 y 5")
    private Integer nivelSeguridad;
    
    private String descripcion;
    
    @NotBlank(message = "El usuario de creación es obligatorio")
    private String usuarioCreacion;

    @JsonProperty("codzona")
    public Integer getCodzona() { return codzona; }
    public void setCodzona(Integer codzona) { this.codzona = codzona; }

    @JsonProperty("nombre")
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    @JsonProperty("distrito")
    public String getDistrito() { return distrito; }
    public void setDistrito(String distrito) { this.distrito = distrito; }

    @JsonProperty("provincia")
    public String getProvincia() { return provincia; }
    public void setProvincia(String provincia) { this.provincia = provincia; }

    @JsonProperty("region")
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    @JsonProperty("pais")
    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    @JsonProperty("latitud")
    public Double getLatitud() { return latitud; }
    public void setLatitud(Double latitud) { this.latitud = latitud; }

    @JsonProperty("longitud")
    public Double getLongitud() { return longitud; }
    public void setLongitud(Double longitud) { this.longitud = longitud; }

    @JsonProperty("nivelSeguridad")
    public Integer getNivelSeguridad() { return nivelSeguridad; }
    public void setNivelSeguridad(Integer nivelSeguridad) { this.nivelSeguridad = nivelSeguridad; }

    @JsonProperty("descripcion")
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    @JsonProperty("usuarioCreacion")
    public String getUsuarioCreacion() { return usuarioCreacion; }
    public void setUsuarioCreacion(String usuarioCreacion) { this.usuarioCreacion = usuarioCreacion; }
}
