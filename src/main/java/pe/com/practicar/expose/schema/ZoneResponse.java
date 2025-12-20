package pe.com.practicar.expose.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@JsonPropertyOrder({
        "nombre",
        "distrito",
        "provincia",
        "region",
        "pais",
        "latitud",
        "longitud",
        "nivelSeguridad",
        "descripcion",
        "activo",
        "usuarioCreacion",
        "usuarioActualizacion",
        "fechaCreacion",
        "fechaActualizacion"
})
public class ZoneResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nombre;
    private String distrito;
    private String provincia;
    private String region;
    private String pais;
    private Double latitud;
    private Double longitud;
    private Integer nivelSeguridad;
    private String descripcion;
    private Boolean activo;
    private String usuarioCreacion;
    private String usuarioActualizacion;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    public ZoneResponse nombre(String nombre) {
        this.nombre = nombre;
        return this;
    }

    @JsonProperty("nombre")
    public String getNombre() { return nombre; }

    public void setNombre(String nombre) { this.nombre = nombre; }

    public ZoneResponse distrito(String distrito) {
        this.distrito = distrito;
        return this;
    }

    @JsonProperty("distrito")
    public String getDistrito() { return distrito; }

    public void setDistrito(String distrito) { this.distrito = distrito; }

    public ZoneResponse provincia(String provincia) {
        this.provincia = provincia;
        return this;
    }

    @JsonProperty("provincia")
    public String getProvincia() { return provincia; }

    public void setProvincia(String provincia) { this.provincia = provincia; }

    public ZoneResponse region(String region) {
        this.region = region;
        return this;
    }

    @JsonProperty("region")
    public String getRegion() { return region; }

    public void setRegion(String region) { this.region = region; }

    public ZoneResponse pais(String pais) {
        this.pais = pais;
        return this;
    }

    @JsonProperty("pais")
    public String getPais() { return pais; }

    public void setPais(String pais) { this.pais = pais; }

    public ZoneResponse latitud(Double latitud) {
        this.latitud = latitud;
        return this;
    }

    @JsonProperty("latitud")
    public Double getLatitud() { return latitud; }

    public void setLatitud(Double latitud) { this.latitud = latitud; }

    public ZoneResponse longitud(Double longitud) {
        this.longitud = longitud;
        return this;
    }

    @JsonProperty("longitud")
    public Double getLongitud() { return longitud; }

    public void setLongitud(Double longitud) { this.longitud = longitud; }

    public ZoneResponse nivelSeguridad(Integer nivelSeguridad) {
        this.nivelSeguridad = nivelSeguridad;
        return this;
    }

    @JsonProperty("nivelSeguridad")
    public Integer getNivelSeguridad() { return nivelSeguridad; }

    public void setNivelSeguridad(Integer nivelSeguridad) { this.nivelSeguridad = nivelSeguridad; }

    public ZoneResponse descripcion(String descripcion) {
        this.descripcion = descripcion;
        return this;
    }

    @JsonProperty("descripcion")
    public String getDescripcion() { return descripcion; }

    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public ZoneResponse activo(Boolean activo) {
        this.activo = activo;
        return this;
    }

    @JsonProperty("activo")
    public Boolean getActivo() { return activo; }

    public void setActivo(Boolean activo) { this.activo = activo; }

    public ZoneResponse usuarioCreacion(String usuarioCreacion) {
        this.usuarioCreacion = usuarioCreacion;
        return this;
    }

    @JsonProperty("usuarioCreacion")
    public String getUsuarioCreacion() { return usuarioCreacion; }

    public void setUsuarioCreacion(String usuarioCreacion) { this.usuarioCreacion = usuarioCreacion; }

    public ZoneResponse usuarioActualizacion(String usuarioActualizacion) {
        this.usuarioActualizacion = usuarioActualizacion;
        return this;
    }

    @JsonProperty("usuarioActualizacion")
    public String getUsuarioActualizacion() { return usuarioActualizacion; }

    public void setUsuarioActualizacion(String usuarioActualizacion) { this.usuarioActualizacion = usuarioActualizacion; }

    public ZoneResponse fechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
        return this;
    }

    @JsonProperty("fechaCreacion")
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }

    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public ZoneResponse fechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
        return this;
    }

    @JsonProperty("fechaActualizacion")
    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ZoneResponse zoneResponse = (ZoneResponse) o;
        return Objects.equals(this.nombre, zoneResponse.nombre) &&
               Objects.equals(this.distrito, zoneResponse.distrito) &&
               Objects.equals(this.provincia, zoneResponse.provincia) &&
               Objects.equals(this.region, zoneResponse.region) &&
               Objects.equals(this.pais, zoneResponse.pais) &&
               Objects.equals(this.latitud, zoneResponse.latitud) &&
               Objects.equals(this.longitud, zoneResponse.longitud) &&
               Objects.equals(this.nivelSeguridad, zoneResponse.nivelSeguridad) &&
               Objects.equals(this.descripcion, zoneResponse.descripcion) &&
               Objects.equals(this.activo, zoneResponse.activo) &&
               Objects.equals(this.usuarioCreacion,  zoneResponse.usuarioCreacion) &&
               Objects.equals(this.usuarioActualizacion, zoneResponse.usuarioActualizacion) &&
               Objects.equals(this.fechaCreacion, zoneResponse.fechaCreacion) &&
               Objects.equals(this.fechaActualizacion, zoneResponse.fechaActualizacion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre, distrito, provincia, region, pais, latitud, longitud,
                            nivelSeguridad, descripcion, activo, usuarioCreacion,
                            usuarioActualizacion, fechaCreacion, fechaActualizacion);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ZoneResponse {\n");
        
        sb.append("    nombre: ").append(toIndentedString(nombre)).append("\n");
        sb.append("    distrito: ").append(toIndentedString(distrito)).append("\n");
        sb.append("    provincia: ").append(toIndentedString(provincia)).append("\n");
        sb.append("    region: ").append(toIndentedString(region)).append("\n");
        sb.append("    pais: ").append(toIndentedString(pais)).append("\n");
        sb.append("    latitud: ").append(toIndentedString(latitud)).append("\n");
        sb.append("    longitud: ").append(toIndentedString(longitud)).append("\n");
        sb.append("    nivelSeguridad: ").append(toIndentedString(nivelSeguridad)).append("\n");
        sb.append("    descripcion: ").append(toIndentedString(descripcion)).append("\n");
        sb.append("    activo: ").append(toIndentedString(activo)).append("\n");
        sb.append("    usuarioCreacion: ").append(toIndentedString(usuarioCreacion)).append("\n");
        sb.append("    usuarioActualizacion: ").append(toIndentedString(usuarioActualizacion)).append("\n");
        sb.append("    fechaCreacion: ").append(toIndentedString(fechaCreacion)).append("\n");
        sb.append("    fechaActualizacion: ").append(toIndentedString(fechaActualizacion)).append("\n");
        sb.append("}");
        return sb.toString();
    }   

    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

}
