package pe.com.practicar.expose.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

public class ZoneCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "Los datos son obligatorios")
    @Valid
    private ZoneDatosCreateRequest datos;

    @JsonProperty("datos")
    public ZoneDatosCreateRequest getDatos() { return datos; }
    public void setDatos(ZoneDatosCreateRequest datos) { this.datos = datos; }
}
