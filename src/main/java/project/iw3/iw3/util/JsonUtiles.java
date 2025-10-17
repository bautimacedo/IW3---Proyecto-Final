package project.iw3.iw3.util;

import java.text.SimpleDateFormat;
import java.util.Locale;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

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
		SimpleDateFormat df = new SimpleDateFormat(defaultFormat, Locale.getDefault());
		SimpleModule module = new SimpleModule();
		if (deser != null) {
			module.addDeserializer(clazz, deser);
		}
		mapper.setDateFormat(df);
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

}


