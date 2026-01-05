package pe.com.practicar.business.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BusinessErrorCodes {

  // Errores de zona
  ZONE_NOT_FOUND("001", "ZONE_NOT_FOUND", "La zona no existe"),
  ZONE_ALREADY_EXISTS("002", "ZONE_ALREADY_EXISTS", "Ya existe una zona con esos datos"),
  ZONE_CREATION_FAILED("003", "ZONE_CREATION_FAILED", "Error al crear la zona en la base de datos"),
  ZONE_UPDATE_FAILED("004", "ZONE_UPDATE_FAILED", "Error al actualizar la zona"),
  ZONE_DELETE_FAILED("005", "ZONE_DELETE_FAILED", "Error al eliminar la zona"),
  ZONE_RETRIEVE_FAILED("006", "ZONE_RETRIEVE_FAILED", "Error al obtener la zona"),
  
  // Errores de validación
  INVALID_COORDINATES("010", "INVALID_COORDINATES", "Las coordenadas no son válidas"),
  INVALID_RISK_LEVEL("011", "INVALID_RISK_LEVEL", "El nivel de riesgo debe estar entre 1 y 10"),
  INVALID_PAGINATION("012", "INVALID_PAGINATION", "Los parámetros de paginación no son válidos"),
  REQUIRED_FIELD_MISSING("013", "REQUIRED_FIELD_MISSING", "Campo requerido faltante"),
  
  // Errores generales
  BUSINESS_ERROR("050", "BUSINESS_ERROR", "Error de negocio"),
  DATABASE_ERROR("051", "DATABASE_ERROR", "Error de base de datos"),
  GENERIC_ERROR("099", "GENERIC_ERROR", "Ha ocurrido un error genérico");

  private final String code;
  private final String title;
  private final String description;
}
