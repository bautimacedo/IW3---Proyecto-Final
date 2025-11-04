package project.iw3.iw3.model;

import project.iw3.iw3.util.JsonUtiles;
import project.iw3.iw3.util.ConstantesJson;

import java.io.IOException;
import java.util.Date;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import lombok.extern.slf4j.Slf4j;
import project.iw3.iw3.model.business.exceptions.BusinessException;
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

	public OrdenJsonDeserializer (IChoferBusiness choferBusiness, ICamionBusiness camionBusiness,
			IClienteBusiness clienteBusiness, IProductoBusiness productoBusiness, ICisternaBusiness cisternaBusiness) {
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
			Orden r = new Orden();
			JsonNode node = jp.getCodec().readTree(jp);
			
			float preset = (float) JsonUtiles.getDouble(node, ConstantesJson.ORDEN_PRESET_ATTRIBUTES, 0);
			Integer numeroOrden = JsonUtiles.getInteger(node, ConstantesJson.ORDEN_NUMERORDEN_ATTRIBUTES, 0); // revisar esto, puede explotar xq tengo unique en la tabla.
			//Date estimatedTime = JsonUtiles.getDate(node, ConstantesJson.ORDER_ESTIMATED_DATE_ATTRIBUTES, new Date());
			Chofer chofer = JsonUtiles.getChofer(node, ConstantesJson.DRIVER_DNI_ATTRIBUTES, choferBusiness);
	        Camion camion = (JsonUtiles.getCamion(node, ConstantesJson.CAMION_PATENTE_ATTRIBUTES, camionBusiness, cisternaBusiness));
	        Cliente cliente = (JsonUtiles.getCliente(node, ConstantesJson.CLIENTE_NOMBRE_ATTRIBUTES, clienteBusiness));
	        Producto producto = JsonUtiles.getProducto(node, ConstantesJson.PRODUCTO_NOMBRE_ATTRIBUTES, productoBusiness);
			
	        log.debug("preset={}, numeroOrden={}, driver={}, truck={}, customer={}, product={}",
	        	    preset, numeroOrden,
	        	    chofer != null, camion != null, cliente != null, producto != null);
	        if (producto != null && cliente != null && camion != null && chofer != null) {
				r.setPreset(preset);
				r.setChofer(chofer);
				r.setCliente(cliente);
				r.setProducto(producto);
				r.setCamion(camion);
				r.setNumeroOrden(numeroOrden);
				r.setEstadoOrden(EstadoOrden.PENDIENTE_PESAJE_INICIAL);
	        }
	        return r;
		}  catch (Exception ex) {
	        	throw new IOException("Error deserializando Orden", ex);
	    }
	}
		

}
