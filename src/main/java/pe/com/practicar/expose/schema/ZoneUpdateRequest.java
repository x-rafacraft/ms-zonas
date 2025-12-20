package pe.com.practicar.expose.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

public class ZoneUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Valid
    private ZoneDatosUpdateRequest datos;

    @JsonProperty("datos")
    public ZoneDatosUpdateRequest getDatos() { return datos; }

    public void setDatos(ZoneDatosUpdateRequest datos) { this.datos = datos; }
}
