package pe.com.practicar.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {
    public static final String RESPONSE_MESSAGE_ERROR_FORMAT_PARAMS = "Debe asignar valor(es) que cumplan con el formato solicitado en el/los siguiente(s) parametro(s): ";
    public static final String RESPONSE_MESSAGE_ERROR_FORMAT_HEADER = "Debe asignar valor(es) que cumplan con el formato solicitado en el/los siguiente(s) parametro(s) de header: ";
    
    public static final String REGEX_NOT_SPECIAL_CHARACTER = "^[a-zA-Z0-9\\s]+$";
    public static final String REGEX_ALPHANUMERIC_WITH_SPACES = "^[a-zA-Z0-9\\sáéíóúÁÉÍÓÚñÑ.,\\-]+$";
    public static final String REGEX_COORDINATES = "^-?\\d+\\.?\\d*$";
    
    public static final String TRANSACTION_ID = "Transaccion-Id";
    public static final String APPLICATION_ID = "Aplicacion-Id";
    public static final String USER_ID = "Usuario-Id";
    
    public static final String FIELD_PATTERN = "field \"";
    public static final String FROM_STRING_PATTERN = "from String \"";
    public static final String FOR_INPUT_STRING_PATTERN = "For input string: \"";
    public static final String QUOTE = "\"";
    public static final String CANNOT_DESERIALIZE = "Cannot deserialize value of type";
    public static final String JAVA_INTEGER = "java.lang.Integer";
    
    public static final String FIELD_NOMBRE = "nombre";
    public static final String FIELD_LATITUD = "latitud";
    public static final String FIELD_LONGITUD = "longitud";
    public static final String FIELD_NIVEL_SEGURIDAD = "nivelSeguridad";
    public static final String FIELD_DESCRIPCION = "descripcion";
}
