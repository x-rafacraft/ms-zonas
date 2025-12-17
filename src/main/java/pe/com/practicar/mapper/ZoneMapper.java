package pe.com.practicar.mapper;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ZoneMapper implements Serializable {

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
}
