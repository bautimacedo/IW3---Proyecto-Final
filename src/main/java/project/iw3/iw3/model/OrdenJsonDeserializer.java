package project.iw3.iw3.model;

import project.iw3.iw3.util.JsonUtiles;
import project.iw3.iw3.util.ConstantesJson;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import lombok.extern.slf4j.Slf4j;
import project.iw3.iw3.model.business.interfaces.ICamionBusiness;
import project.iw3.iw3.model.business.interfaces.IChoferBusiness;
import project.iw3.iw3.model.business.interfaces.ICisternaBusiness;
import project.iw3.iw3.model.business.interfaces.IClienteBusiness;
import project.iw3.iw3.model.business.interfaces.IProductoBusiness;
import project.iw3.iw3.model.enums.EstadoOrden;

@Slf4j
public class OrdenJsonDeserializer extends StdDeserializer<Orden> {

	private ICamionBusiness camionBusiness;
	private IProductoBusiness productoBusiness;
	private IChoferBusiness choferBusiness;
	private IClienteBusiness clienteBusiness;
	private ICisternaBusiness cisternaBusiness;

	public OrdenJsonDeserializer(IChoferBusiness choferBusiness, ICamionBusiness camionBusiness,
			IClienteBusiness clienteBusiness, IProductoBusiness productoBusiness,
			ICisternaBusiness cisternaBusiness) {
		super(Orden.class);
		this.choferBusiness = choferBusiness;
		this.camionBusiness = camionBusiness;
		this.clienteBusiness = clienteBusiness;
		this.productoBusiness = productoBusiness;
		this.cisternaBusiness = cisternaBusiness;
	}

	@Override
	public Orden deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
		try {
			log.info("🟢 Iniciando deserialización de Orden externa...");
			JsonNode node = jp.getCodec().readTree(jp);
			if (node == null) {
				log.error("❌ El nodo raíz del JSON es null.");
				throw new IOException("JSON vacío o inválido");
			}

			log.debug("📦 JSON completo recibido:\n{}", node.toPrettyString());

			float preset = (float) JsonUtiles.getDouble(node, ConstantesJson.ORDEN_PRESET_ATTRIBUTES, 0);
			Integer numeroOrden = JsonUtiles.getInteger(node, ConstantesJson.ORDEN_NUMERORDEN_ATTRIBUTES, 0);

			log.debug("🔢 Datos base: numeroOrden={}, preset={}", numeroOrden, preset);

			// ------------------- CHOFER -------------------
			log.debug("🧍 Buscando chofer...");
			JsonNode choferNode = JsonUtiles.getJsonNode(node, ConstantesJson.CHOFER_NODE_ATTRIBUTES);
			log.debug("📄 Nodo chofer encontrado: {}", choferNode != null ? choferNode.toPrettyString() : "❌ null");
			Chofer chofer = JsonUtiles.getChofer(node, ConstantesJson.DRIVER_DNI_ATTRIBUTES, choferBusiness);
			log.debug("🧾 Chofer después de getChofer(): {}", chofer != null ? chofer.toString() : "❌ null");

			// ------------------- CAMION -------------------
			log.debug("🚛 Buscando camión...");
			JsonNode camionNode = JsonUtiles.getJsonNode(node, ConstantesJson.CAMION_NODE_ATTRIBUTES);
			log.debug("📄 Nodo camión encontrado: {}", camionNode != null ? camionNode.toPrettyString() : "❌ null");
			String patenteDebug = JsonUtiles.getString(camionNode != null ? camionNode : node,
					ConstantesJson.CAMION_PATENTE_ATTRIBUTES, "no encontrado");
			log.debug("🔍 Patente detectada desde JSON: {}", patenteDebug);
			Camion camion = JsonUtiles.getCamion(node, ConstantesJson.CAMION_PATENTE_ATTRIBUTES, camionBusiness,
					cisternaBusiness);
			log.debug("🚚 Camión después de getCamion(): {}", camion != null ? camion.toString() : "❌ null");

			// ------------------- CLIENTE -------------------
			log.debug("🏢 Buscando cliente...");
			JsonNode clienteNode = JsonUtiles.getJsonNode(node, ConstantesJson.CLIENTE_NODE_ATTRIBUTES);
			log.debug("📄 Nodo cliente encontrado: {}", clienteNode != null ? clienteNode.toPrettyString() : "❌ null");
			Cliente cliente = JsonUtiles.getCliente(node, ConstantesJson.CLIENTE_NOMBRE_ATTRIBUTES, clienteBusiness);
			log.debug("💳 Cliente después de getCliente(): {}", cliente != null ? cliente.toString() : "❌ null");

			// ------------------- PRODUCTO -------------------
			log.debug("🧪 Buscando producto...");
			JsonNode productoNode = JsonUtiles.getJsonNode(node, ConstantesJson.PRODUCTO_NODE_ATTRIBUTES);
			log.debug("📄 Nodo producto encontrado: {}", productoNode != null ? productoNode.toPrettyString() : "❌ null");
			Producto producto = JsonUtiles.getProducto(node, ConstantesJson.PRODUCTO_NOMBRE_ATTRIBUTES,
					productoBusiness);
			log.debug("⚗️ Producto después de getProducto(): {}", producto != null ? producto.toString() : "❌ null");

			// ------------------- RESULTADO -------------------
			log.info("✅ Resultado intermedio -> Chofer={}, Camion={}, Cliente={}, Producto={}",
					chofer != null, camion != null, cliente != null, producto != null);

			Orden r = new Orden();
			if (producto != null && cliente != null && camion != null && chofer != null) {
				r.setPreset(preset);
				r.setChofer(chofer);
				r.setCliente(cliente);
				r.setProducto(producto);
				r.setCamion(camion);
				r.setNumeroOrden(numeroOrden);
				r.setEstadoOrden(EstadoOrden.PENDIENTE_PESAJE_INICIAL);
				log.info("✅ Orden creada exitosamente con estado: {}", r.getEstadoOrden());
			} else {
				log.error("❌ Faltan entidades requeridas para construir la orden:");
				if (producto == null)
					log.error("   - Producto es null");
				if (cliente == null)
					log.error("   - Cliente es null");
				if (camion == null)
					log.error("   - Camión es null");
				if (chofer == null)
					log.error("   - Chofer es null");
			}

			return r;

		} catch (Exception ex) {
			log.error("💥 Error deserializando Orden: {}", ex.getMessage(), ex);
			throw new IOException("Error deserializando Orden", ex);
		}
	}
}
