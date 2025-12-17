package pe.com.practicar.expose.schema;

import java.io.Serializable;
import java.util.Objects;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;

public class ZonePaginateResponse implements Serializable{

    private static final long serialVersionUID = 1L;

    @Valid
    private List<@Valid ZoneResponse> zone = new ArrayList<>();

    private Integer paginaActual;
    private Integer tamanioPagina;
    private Boolean existeSiguientePagina;

    public ZonePaginateResponse zone(List<@Valid ZoneResponse> zone) {
        this.zone = zone;
        return this;
    }

    public ZonePaginateResponse addZoneItem(ZoneResponse zoneItem) {
        if (this.zone == null) {
            this.zone = new ArrayList<>();
        }
        this.zone.add(zoneItem);
        return this;
    }



    @Valid
    @JsonProperty("zone")
    public List<@Valid ZoneResponse> getZone() {
        return zone;
    }

    public void setZone(List<@Valid ZoneResponse> zone) {
        this.zone = zone;
    }

    public ZonePaginateResponse paginaActual(Integer paginaActual) {
        this.paginaActual = paginaActual;
        return this;
    }

    @JsonProperty("paginaActual")
    public Integer getPaginaActual() {
        return paginaActual;
    }

    public void setPaginaActual(Integer paginaActual) {
        this.paginaActual = paginaActual;
    }

    public ZonePaginateResponse tamanioPagina(Integer tamanioPagina) {
        this.tamanioPagina = tamanioPagina;
        return this;
    }

    @JsonProperty("tamanioPagina")
    public Integer getTamanioPagina() {
        return tamanioPagina;
    }

    public void setTamanioPagina(Integer tamanioPagina) {
        this.tamanioPagina = tamanioPagina;
    }

    public ZonePaginateResponse existeSiguientePagina(Boolean existeSiguientePagina) {
        this.existeSiguientePagina = existeSiguientePagina;
        return this;
    }

    @JsonProperty("existeSiguientePagina")
    public Boolean getExisteSiguientePagina() {
        return existeSiguientePagina;
    }

    public void setExisteSiguientePagina(Boolean existeSiguientePagina) {
        this.existeSiguientePagina = existeSiguientePagina;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ZonePaginateResponse zonePaginateResponse = (ZonePaginateResponse) o;
        return Objects.equals(this.zone, zonePaginateResponse.zone) &&
               Objects.equals(this.paginaActual, zonePaginateResponse.paginaActual) &&
               Objects.equals(this.tamanioPagina, zonePaginateResponse.tamanioPagina) &&
               Objects.equals(this.existeSiguientePagina, zonePaginateResponse.existeSiguientePagina);
    }

    @Override
    public int hashCode() {
        return Objects.hash(zone, paginaActual, tamanioPagina, existeSiguientePagina);
    }   

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ZonePaginateResponse {\n");
        
        sb.append("    zone: ").append(toIndentedString(zone)).append("\n");
        sb.append("    paginaActual: ").append(toIndentedString(paginaActual)).append("\n");
        sb.append("    tamanioPagina: ").append(toIndentedString(tamanioPagina)).append("\n");
        sb.append("    existeSiguientePagina: ").append(toIndentedString(existeSiguientePagina)).append("\n");
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
