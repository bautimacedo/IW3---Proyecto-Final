package project.iw3.iw3.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import project.iw3.iw3.model.Camion;
import project.iw3.iw3.model.Chofer;
import project.iw3.iw3.model.Cliente;
import project.iw3.iw3.model.Producto;
import project.iw3.iw3.model.business.exceptions.BusinessException;
import project.iw3.iw3.model.business.interfaces.ICamionBusiness;
import project.iw3.iw3.model.business.interfaces.IChoferBusiness;
import project.iw3.iw3.model.business.interfaces.ICisternaBusiness;
import project.iw3.iw3.model.business.interfaces.IClienteBusiness;
import project.iw3.iw3.model.business.interfaces.IProductoBusiness;
import io.micrometer.common.lang.Nullable;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;


public final class JsonUtiles {
	private static final Logger log = LoggerFactory.getLogger(JsonUtiles.class);



	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static ObjectMapper getObjectMapper(Class clazz, StdSerializer ser, String dateFormat) {
		ObjectMapper mapper = new ObjectMapper();
		String defaultFormat = "yyyy-MM-dd'T'HH:mm:ssZ";
		if (dateFormat != null)
			defaultFormat = dateFormat;
		SimpleDateFormat df = new SimpleDateFormat(defaultFormat, Locale.getDefault());
		SimpleModule module = new SimpleModule();
		if (ser != null) {
			module.addSerializer(clazz, ser);
		}
		mapper.setDateFormat(df);
		mapper.registerModule(module);
		return mapper;

	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static ObjectMapper getObjectMapper(Class clazz, StdDeserializer deser, String dateFormat) {
		ObjectMapper mapper = new ObjectMapper();
		String defaultFormat = "yyyy-MM-dd'T'HH:mm:ssZ";
		if (dateFormat != null)
			defaultFormat = dateFormat;
		SimpleDateFormat df = new SimpleDateFormat(defaultFormat, Locale.getDefault()); //permite configurar como jackson
		// interpretara las fechas al convertir JSON --> Date.
		// Por defecto usa ISO-8601 con zona horaria (2025-11-03T19:00:00-0300).
		// Si le pasás un formato distinto (p. ej. "dd/MM/yyyy HH:mm"), usará ese.
		
		SimpleModule module = new SimpleModule();
		if (deser != null) {
			module.addDeserializer(clazz, deser);
		}
		mapper.setDateFormat(df);//
		mapper.registerModule(module);
		return mapper;
	}

	/**
	 * Obtiene una cadena con la siguiente lógica:
	 * 1) Busca en cada uno de los atributos definidos en el arreglo "attrs",
	 *    el primero que encuentra será el valor retornado.
	 * 2) Si no se encuentra ninguno de los atributos del punto 1), se
	 *    retorna "defaultValue".
	 * Ejemplo: supongamos que "node" represente: {"code":"c1, "codigo":"c11", "stock":true}
	 *   getString(node, String[]{"codigo","cod"},"-1") retorna: "cl1"
	 *   getString(node, String[]{"cod_prod","c_prod"},"-1") retorna: "-1"
	 * @param node
	 * @param attrs
	 * @param defaultValue
	 * @return
	 */

	public static String getString(JsonNode node, String[] attrs, String defaultValue) {
		String r = null;
		for (String attr : attrs) {
			if (node.get(attr) != null) {
				r = node.get(attr).asText();
				break;
			}
		}
		if (r == null)
			r = defaultValue;
		return r;
	}
	
	public static float getFloat(JsonNode node, String[] attrs, float defaultValue) {
	    for (String attr : attrs) {
	        if (node.get(attr) != null && (node.get(attr).isFloat() || node.get(attr).isDouble())) {
	            return node.get(attr).floatValue(); // Utilizamos floatValue() para obtener un valor float
	        }
	    }
	    return defaultValue; // Si no se encuentra o el valor no es adecuado, se devuelve el valor por defecto
	}

	
	public static int getInteger(JsonNode node, String[] attrs, int defaultValue) {
	    for (String attr : attrs) {
	        if (node.get(attr) != null && node.get(attr).isInt()) {
	            return node.get(attr).asInt();
	        }
	    }
	    return defaultValue;
	}

        public static double getDouble(JsonNode node, String[] attrs, double defaultValue) {
                Double r = null;
                for (String attr : attrs) {
                        JsonNode value = node.get(attr);
                        if (value != null && value.isNumber()) {
                                r = value.asDouble();
                                break;
                        }
                }
                if (r == null)
                        r = defaultValue;
                return r;
        }
	
	public static Long getLong(JsonNode node, String[] attrs, long defaultValue) {
	    Long r = null;
	    for (String attr : attrs) {
	        if (node.get(attr) != null && node.get(attr).canConvertToLong()) {
	            r = node.get(attr).asLong();
	            break;
	        }
	    }
	    if (r == null)
	        r = defaultValue;
	    return r;
	}
	
	

	public static boolean getBoolean(JsonNode node, String[] attrs, boolean defaultValue) {
		Boolean r = null;
		for (String attr : attrs) {
			if (node.get(attr) != null && node.get(attr).isBoolean()) {
				r = node.get(attr).asBoolean();
				break;
			}
		}
		if (r == null)
			r = defaultValue;
		return r;
	}
	
	public static Date getDate(JsonNode node, String[] strings, Date defaultValue) {
	    if (node == null || strings == null) return defaultValue;

	    for (String attr : strings) {
	        if (attr == null) continue;
	        JsonNode v = node.get(attr);
	        if (v == null || v.isNull()) continue;

	        // 1) Epoch (ms o s)
	        if (v.isNumber()) {
	            long raw = v.asLong();
	            // si parece segundos (<= 10 dígitos), convierto a ms
	            if (Math.abs(raw) < 1_000_000_000_000L) raw *= 1000L;
	            return new Date(raw);
	        }

	        // 2) Texto → intentar varios formatos
	        if (v.isTextual()) {
	            String s = v.asText().trim();
	            if (s.isEmpty()) continue;

	            // ISO con Z/offset (p.ej. 2025-10-31T14:30:00Z o ...-03:00)
	            try { return Date.from(Instant.parse(s)); } catch (Exception ignore) {}
	            try { return Date.from(OffsetDateTime.parse(s).toInstant()); } catch (Exception ignore) {}
	            try { return Date.from(ZonedDateTime.parse(s).toInstant()); } catch (Exception ignore) {}

	            // ISO local sin zona: 2025-10-31T14:30:00
	            try {
	                LocalDateTime ldt = LocalDateTime.parse(s, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
	                return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
	            } catch (Exception ignore) {}

	            // Solo fecha: 2025-10-31
	            try {
	                LocalDate d = LocalDate.parse(s, DateTimeFormatter.ISO_LOCAL_DATE);
	                return Date.from(d.atStartOfDay(ZoneId.systemDefault()).toInstant());
	            } catch (Exception ignore) {}

	            // Formatos comunes extra
	            try {
	                DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss[.SSS]");
	                LocalDateTime ldt = LocalDateTime.parse(s, f);
	                return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
	            } catch (Exception ignore) {}
	            try {
	                // ej: 2025-10-31T14:30:00-0300
	                DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");
	                return Date.from(OffsetDateTime.parse(s, f).toInstant());
	            } catch (Exception ignore) {}
	            try {
	                // ej: 2025-10-31T14:30:00-03:00
	                DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
	                return Date.from(OffsetDateTime.parse(s, f).toInstant());
	            } catch (Exception ignore) {}
	        }
	    }
	    return defaultValue;
	}
	
	  public static float getValue(JsonNode node, String[] attrs, float defaultValue) {
	        Float r = null;
	        for (String attr : attrs) {
	            if (node.get(attr) != null) {
	                // Intentamos manejar el valor como float sin depender de si el tipo es específicamente un float
	                if (node.get(attr).isFloat() || node.get(attr).isDouble() || node.get(attr).isInt()) {
	                    r = node.get(attr).floatValue(); // Convertimos cualquiera de estos tipos a float
	                    break;
	                }
	            }
	        }
	        if (r == null)
	            r = defaultValue;
	        return r;
	    }
	  
	  //esto sirve para buscar dentro de un json, un objeto hijo o subnodo que puede tener
	  //distintos nombres.
	  //node: el nodo JSON actual, por ejemplo, el objeto raíz o un subobjeto (tipo JsonNode).
	  //attrs: un arreglo con nombres posibles de subnodos que querés encontrar.
	  // Recorre los nombres dentro de attrs. Si lo encuentra devuelve r, sino null.
	    public static JsonNode getJsonNode(JsonNode node, String[] attrs) {
	        JsonNode r = null;
	        for (String attr : attrs) {
	            if (node.get(attr) != null) {
	                r = node.get(attr);
	                break;
	            }
	        }
	        return r;
	    }
	  
	  //construye objeto chofer a partir de un json.
	  // ejemplo: 
	    /*
	    {
  		"orderNumber": 123,
  		"driver": {
    		"dni": "40123456",
    		"nombre": "Juan",
    		"appellido": "Pérez"
  			}
		}
	     * */
	    
	    //entonces quedaria driverNode = {"document":"40123456","name":"Juan","lastname":"Pérez"}.
	    
	    
	    
	// Objetivo --> extraer el identificador del chofer (DNI) del JSON y luego usar la lógica de negocio (choferBusiness) 
	// para obtener o crear la entidad Chofer que se utilizará en la Orden.   
	// JsonNode root: Es el nodo JSON raíz del body completo de la orden que se está deserializando.
	// String[] attrs: Es un array de posibles nombres de atributos para el DNI del chofer (adicionales a los definidos en ConstantesJson).
	
	public static Chofer getChofer(JsonNode root, String[] attrs, IChoferBusiness choferBusiness) throws BusinessException {
		  
		    // 1) Buscar el nodo correspondiente al chofer
		
		    JsonNode choferNode = getJsonNode(root, ConstantesJson.CHOFER_NODE_ATTRIBUTES);
		    
		    if (choferNode == null || choferNode.isEmpty()) {
		        log.error("JSON sin nodo de chofer valido");
		        throw new BusinessException("No se encontro el nodo de chofer en el JSON");
		    }
		
		    // 2) Extraer DNI del chofer usando las posibles claves configuradas
		    // firts nonblank toma varias cadenas y devuelve la primera que no sea nula ni este vacia.
		    
		    String dni = firstNonBlank(
		        getString(choferNode, ConstantesJson.DRIVER_DNI_ATTRIBUTES, null),
		        getString(choferNode, attrs, null)
		    );
		
		    if (dni == null) {
		        log.error("Chofer sin dni");
		        throw new BusinessException("Chofer sin dni en JSON");
		    }
		
		    // 3) Extraer nombre y apellido si vienen en el json
		    
		    String nombre = getString(choferNode, ConstantesJson.DRIVER_NOMBRE_ATTRIBUTES, null);
		    String apellido = getString(choferNode, ConstantesJson.DRIVER_APELLIDO_ATTRIBUTES, null);
		
		    try {
		    	
		        // 4) Crear o recuperar el chofer existente
		    	
		        Chofer chofer = choferBusiness.loadOrCreate(dni, nombre, apellido);
		        return chofer;
		        
		    } catch (Exception e) {
		        log.error("Error procesando chofer {}: {}", dni, e.getMessage());
		        throw new BusinessException("Error procesando chofer: " + e.getMessage(), e);
		    }
	}
	  
	   public static Camion getCamion(JsonNode root, String[] attrs, ICamionBusiness camionBusiness, ICisternaBusiness cisternaBusiness, @Nullable float preset) throws BusinessException, IOException {

			    JsonNode camionNode = getJsonNode(root, ConstantesJson.CAMION_NODE_ATTRIBUTES);
			    
			    if (camionNode == null || camionNode.isNull()) {
			        log.error("JSON sin camion ({}).", (Object) ConstantesJson.CAMION_NODE_ATTRIBUTES);
			        throw new BusinessException("Falta el nodo de camión en el JSON.");
			    }
			
			   // vamos a buscar por patente.
			    String patente = getString(camionNode, ConstantesJson.CAMION_PATENTE_ATTRIBUTES, null);
			
			    if (patente == null) {
			        log.error("Camion sin patente en el JSON. Claves aceptadas: {}", (Object) ConstantesJson.CAMION_PATENTE_ATTRIBUTES);
			        throw new BusinessException("Camion: 'patente' es obligatoria.");
			    }
			
			    // descripción opcional
			    String descripcion = getString(camionNode, ConstantesJson.CAMION_DESCRIPCION_ATTRIBUTES, null);
			
			    //deberia usar proveniente de ConstantesJson pero vamos a dejar que la unica forma sea mediante tanks
			    JsonNode cisternasNode = camionNode.hasNonNull("tanks") ? camionNode.get("tanks") : null; 
			    float sumatoria_preset = 0;
			    
			    //si vienen cisternas en el json:
			    if(cisternasNode != null) {
			    	for (JsonNode cisternas : cisternasNode) {
				    	sumatoria_preset += (float) JsonUtiles.getDouble(cisternas, ConstantesJson.CISTERNA_CAPACIDAD_LITROS_ATTRIBUTES, 0);
				    }
				    if(sumatoria_preset < preset) {
				    	log.error("El volumen solicitado (preset=" + preset + ") es superior a la capacidad total de las cisternas (" + sumatoria_preset + ").");
				    	throw new IOException("El volumen solicitado (preset=" + preset + ") es superior a la capacidad total de las cisternas (" + sumatoria_preset + ").");
				    }
			    }
			    // y si no viene ninguna cisterna? tendria que ver que el camion tenga asociadas cisternas?
			
			    // lookup / creación
			    try {
			        Camion camion = camionBusiness.loadOrCreate(patente, descripcion, cisternasNode);
			        
			        log.info("Camión listo (loadOrCreate): patente={}", camion.getPatente());
			        return camion;
			        
			    } catch (BusinessException be) {
			        log.error("Error en loadOrCreate(camión): {}", be.getMessage(), be);
			        throw be;
			        
			    } catch (Exception e) {
			        log.error("Error inesperado procesando camión: {}", e.getMessage(), e);
			        throw new BusinessException("Error procesando camión: " + e.getMessage(), e);
			    }
	   }

	  
	  
		// clase utilitaria para getCamion
		public static String firstNonBlank(String... values) {
			    if (values == null) return null;
			    for (String v : values) {
			        if (v != null && !v.trim().isEmpty()) {
			            return v.trim();
			        }
			    }
			    return null;
		}

	  public static Producto getProducto(JsonNode root, String[] attrs, IProductoBusiness productoBusiness) throws BusinessException {
    // 1) Buscar el nodo del producto, por ejemplo "product" o similar
    JsonNode productoNode = getJsonNode(root, ConstantesJson.PRODUCTO_NODE_ATTRIBUTES);
    if (productoNode == null || productoNode.isEmpty()) {
        log.error("JSON sin nodo de producto valido");
        throw new BusinessException("No se encontro el nodo de producto en el JSON");
    }

    // 2) Extraer el nombre usando las posibles claves configuradas en ConstantesJson
    String nombre = firstNonBlank(
        getString(productoNode, ConstantesJson.PRODUCTO_NOMBRE_ATTRIBUTES, null),
        getString(productoNode, attrs, null)
    );

    if (nombre == null) {
        log.error("Producto sin nombre");
        throw new BusinessException("Producto sin nombre en JSON");
    }

    // 3) Extraer descripcion si esta
    String descripcion = getString(productoNode, new String[]{"descripcion", "description", "detail"}, null);
    float temperatura_umbral = getFloat(productoNode, new String[]{"temp", "temperatura_umbral", "tmp"}, -0.5f);


    try {
        // 4) Crear o cargar el producto (sin lanzar excepcion si ya existe)
        Producto producto = productoBusiness.loadOrCreate(nombre, descripcion, temperatura_umbral);
        return producto;
    } catch (Exception e) {
        log.error("Error procesando producto {}: {}", nombre, e.getMessage());
        throw new BusinessException("Error procesando producto: " + e.getMessage(), e);
    }
}
public static Cliente getCliente(JsonNode root, String[] attrs, IClienteBusiness clienteBusiness) throws BusinessException {
    // 1) Buscar el nodo de cliente (por ejemplo "customer" o similar)
    JsonNode clienteNode = getJsonNode(root, ConstantesJson.CLIENTE_NODE_ATTRIBUTES);
    if (clienteNode == null || clienteNode.isEmpty()) {
        log.error("JSON sin nodo de cliente valido");
        throw new BusinessException("No se encontro el nodo de cliente en el JSON");
    }

    // 2) Extraer el nombre de empresa usando las posibles claves
    String nombreEmpresa = firstNonBlank(
        getString(clienteNode, ConstantesJson.CLIENTE_NOMBRE_ATTRIBUTES, null),
        getString(clienteNode, attrs, null)
    );

    if (nombreEmpresa == null) {
        log.error("Cliente sin nombreEmpresa");
        throw new BusinessException("Cliente sin nombreEmpresa en JSON");
    }

    // 3) Extraer el email si esta presente
    String email = getString(clienteNode, new String[]{"email", "correo", "contact"}, null);

    try {
        // 4) Crear o cargar el cliente existente
        Cliente cliente = clienteBusiness.loadOrCreate(nombreEmpresa, email);
        return cliente;
    } catch (Exception e) {
        log.error("Error procesando cliente {}: {}", nombreEmpresa, e.getMessage());
        throw new BusinessException("Error procesando cliente: " + e.getMessage(), e);
    }
}

			  
}


