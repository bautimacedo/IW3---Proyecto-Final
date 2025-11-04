package project.iw3.iw3.util;

import java.text.SimpleDateFormat;
import java.util.Locale;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

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

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;


public final class JsonUtiles {
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
			if (node.get(attr) != null && node.get(attr).isDouble()) {
				r = node.get(attr).asDouble();
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
	  public static Chofer getChofer(JsonNode node, String[] attrs, IChoferBusiness choferBusiness) {
		  JsonNode choferNode = getJsonNode(node, ConstantesJson.CHOFER_NODE_ATTRIBUTES); 
		  //buscamos el dni
		  if (choferNode == null) return null;
		  
		  String dniChofer = null;
			  
		  for (String attr : attrs) { //aca busca DRIVER_DNI_ATTRIBUTES
			  if (choferNode.get(attr) != null) {
				  dniChofer = choferNode.get(attr).asText();
	              break;
	              }
	      } 
		  
		  if (dniChofer == null) return null;
		  
		  try{
			  
			  Chofer dto = BuildEntityUtiles.choferConstructor(choferNode);
			  return choferBusiness.loadOrCreate(dto); //aca tenemos que crear al chofer
			  
		  } catch(Exception e){
			  
			  return null;
			  
		  } 
	  }
	  
	  public static Camion getCamion(JsonNode node, String[] attrs, ICamionBusiness camionBusiness, ICisternaBusiness cisternaBusiness) {
		  JsonNode camionNode = getJsonNode(node, ConstantesJson.CAMION_NODE_ATTRIBUTES); 
		  if(camionNode == null) return null;
		  
		  String patenteCamion = getString(camionNode, attrs, null);  // Obtener placa del camión desde los atributos
		  
		  if(patenteCamion == null) return null;
		  
		  JsonNode cisternaNode = getJsonNode(camionNode, ConstantesJson.CISTERNA_NODE_ATTRIBUTES);
		  
		  try {
			  
			  Camion dto = BuildEntityUtiles.camionConstructor(camionNode, cisternaNode);
			  return camionBusiness.loadOrCreate(dto); //creamos o traemos el camion ATENCION EN ESTE CASO EL CAMION SIEMPRE TIENE LAS MISMAS CISTERNAS
			  
		  } catch (Exception e) {
			  
			  return null;
		  }
		  
		  
	  }
	  
	  public static Cliente getCliente(JsonNode node, String[] attrs, IClienteBusiness clienteBusiness) {
		  JsonNode clienteNode = getJsonNode(node, ConstantesJson.CLIENTE_NODE_ATTRIBUTES); //buscamos a cliente en el json
		  
		  if(clienteNode == null) return null;
		  
		  String nombreCliente = null;
		  
		  //recorremos todos los campos hasta encontrar CLIENTE_NOMBRE_ATTRIBUTES
		  for (String attr : attrs) {
			    if (clienteNode.get(attr) != null) {
			    	nombreCliente = clienteNode.get(attr).asText();
			        break;
			    }
		  }
		  //si encontramos el nombre, construimos el cliente.
		  if (nombreCliente == null) return null;
		  try {
			  Cliente dto = BuildEntityUtiles.clienteConstructor(clienteNode);
			  return clienteBusiness.loadOrCreate(dto);
		  } catch(Exception e) {
			  return null;
		  } 
	  }
	  
	  
	  
	  public static Producto getProducto(JsonNode node, String[] attrs, IProductoBusiness productoBusiness) {
		  JsonNode productoNode = getJsonNode(node, ConstantesJson.PRODUCTO_NODE_ATTRIBUTES); //buscamos a cliente en el json
		  
		  if(productoNode == null) return null;
		  
		  String nombreProducto = null;
		  
		  for (String attr : attrs) {
			    if (productoNode.get(attr) != null) {
			    	nombreProducto = productoNode.get(attr).asText();
			        break;
			    }
		  }
		  
		  if (nombreProducto == null) return null;
		  
		  try {
			  Producto dto = BuildEntityUtiles.productoConstructor(productoNode);
			  return productoBusiness.loadOrCreate(dto);
			  
		  } catch(Exception e) {
			  return null;
		  }
		  
	  }
			  
}


