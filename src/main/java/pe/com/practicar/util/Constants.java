package pe.com.practicar.util;

public class Constants {
    // Mensajes de error
    public static final String RESPONSE_MESSAGE_ERROR_FORMAT_PARAMS = "Debe asignar valor(es) que cumplan con el formato solicitado en el/los siguiente(s) parametro(s): ";
    public static final String RESPONSE_MESSAGE_ERROR_FORMAT_HEADER = "Debe asignar valor(es) que cumplan con el formato solicitado en el/los siguiente(s) parametro(s) de header: ";
    
    // Regex para validaciones
    public static final String REGEX_NOT_SPECIAL_CHARACTER = "^[a-zA-Z0-9\\s]+$";
    public static final String REGEX_ALPHANUMERIC_WITH_SPACES = "^[a-zA-Z0-9\\sáéíóúÁÉÍÓÚñÑ.,\\-]+$";
    public static final String REGEX_COORDINATES = "^-?\\d+\\.?\\d*$";
    
    // Headers
    public static final String TRANSACTION_ID = "Transaccion-Id";
    public static final String APPLICATION_ID = "Aplicacion-Id";
    public static final String USER_ID = "Usuario-Id";
}
