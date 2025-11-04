package project.iw3.iw3.model.business;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import project.iw3.iw3.model.Orden;
import project.iw3.iw3.model.business.exceptions.BusinessException;
import project.iw3.iw3.model.business.exceptions.FoundException;
import project.iw3.iw3.model.business.exceptions.NotFoundException;
import project.iw3.iw3.model.business.interfaces.ICamionBusiness;
import project.iw3.iw3.model.business.interfaces.IChoferBusiness;
import project.iw3.iw3.model.business.interfaces.ICisternaBusiness;
import project.iw3.iw3.model.business.interfaces.IClienteBusiness;
import project.iw3.iw3.model.business.interfaces.IOrdenBusiness;
import project.iw3.iw3.model.business.interfaces.IProductoBusiness;
import project.iw3.iw3.model.enums.EstadoOrden;
import project.iw3.iw3.model.persistence.OrdenRepository;
import project.iw3.iw3.util.GeneradorDePasswordActivacionPaso2;
import project.iw3.iw3.util.JsonUtiles;
import project.iw3.iw3.model.OrdenJsonDeserializer;


@Service
@Slf4j
public class OrdenBusiness implements IOrdenBusiness {

	@Autowired
    private OrdenRepository ordenRepository;
	
	
	@Override
	public List<Orden> list() throws BusinessException {
		try {
            return ordenRepository.findAll();
        } catch (Exception e) {
        	log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).message(e.getMessage()).build();
        }
	}

	@Override
	public Orden load(long id) throws NotFoundException, BusinessException { //cargar una orden por id
		Optional<Orden> o;
		try {
			o = ordenRepository.findById(id);
		}catch(Exception e){
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).message(e.getMessage()).build();
		}
		if(o.isEmpty()) {
			throw NotFoundException.builder().message("No se encuentra la orden con id=" + id).build();
		}
		return o.get();
		
	}

	@Override
	public Orden update(Orden orden) throws FoundException, NotFoundException, BusinessException {
		load(orden.getId());
		try {
			return ordenRepository.save(orden);
		}catch(Exception e) {
			log.error(e.getMessage(),e);
			throw BusinessException.builder().message("Error al Actualizar Orden").build();
		}
	}

	@Override
	public void delete(long id) throws NotFoundException, BusinessException {
		load(id);
		try {
			ordenRepository.deleteById(id);
		}catch(Exception e) {
			log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
		}
		
	}

	@Override
	public Orden add(Orden orden) throws FoundException, BusinessException {
		try {
			load(orden.getId());
			throw FoundException.builder().message("Se encuentró la orden id=" + orden.getId()).build();	
		}catch(NotFoundException e) {
			
		}
		
		try {
			return ordenRepository.save(orden);
		}catch(Exception e) {
			log.error(e.getMessage(), e);
			 throw BusinessException.builder().message("Error al Crear Nueva Orden").build();
		}
	}
	
	@Autowired
    private IClienteBusiness clienteBusiness;

    @Autowired
    private ICamionBusiness camionBusiness;

    @Autowired

    private ICisternaBusiness cisternaBusiness;

    @Autowired
    private IProductoBusiness productoBusiness;

    @Autowired
    private IChoferBusiness choferBusiness;

	
	//con esto transormamos un json en una orden ya lista. Esto es el punto 1).
	@Override
	public Orden addExternal(String json) throws FoundException, BusinessException {
        ObjectMapper mapper = JsonUtiles.getObjectMapper(Orden.class, new OrdenJsonDeserializer(
                
                choferBusiness, 
                camionBusiness, 
                clienteBusiness, 
                productoBusiness, 
                cisternaBusiness), 
        		
        		null); // formato de fecha por defecto
        
        Orden orden;
        
        try {
            orden = mapper.readValue(json, Orden.class);
        } catch (IOException e) {
        	
        	// JSON mal formado o tipos incorrectos -> tratá como 400
            log.error(e.getMessage(), e);
            throw BusinessException.builder().message("JSON inválido en addExternal: " + e.getMessage()).ex(e).build();
        }
        return add(orden);
		
		
	}

	
	//PUNTO 2
	@Override
	public Orden registrarPesoInicial(String patente, float tara)
			throws BusinessException, NotFoundException, FoundException {
		
		Optional<Orden> ordenEncontrada;
		
		try {
			ordenEncontrada = ordenRepository.findByCamion_PatenteAndEstadoOrden(patente, EstadoOrden.PENDIENTE_PESAJE_INICIAL);
		} catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
		if (ordenEncontrada.isEmpty()) {
            throw NotFoundException.builder().message("No hay una orden para cargar con el camion con patente " + patente).build();
        }
		
		// ahora vamos con la contrasenia
		int password = Integer.parseInt(GeneradorDePasswordActivacionPaso2.generarPassword());
		
		ordenEncontrada.get().setPassword(password);
		ordenEncontrada.get().setTara(tara);
		ordenEncontrada.get().setEstadoOrden(EstadoOrden.CON_PESAJE_INICIAL);
		this.update(ordenEncontrada.get());
		return ordenEncontrada.get();
		
		
		

	}

	//PUNTO4
	@Override
	public Orden cerrarOrden(Long orderId) throws BusinessException, NotFoundException, FoundException {
		
		Optional<Orden> ordenEncontrada;
		
		try {
			ordenEncontrada = ordenRepository.findById(orderId);
		}catch(Exception e) {
			log.error(e.getMessage(), e);
            throw new BusinessException("Error al encontrar la orden por ID", e);
		} if (ordenEncontrada.isEmpty()) {
            throw new NotFoundException("No se ha encontrado la orden");
        }
		
		if(ordenEncontrada.get().getEstadoOrden() != EstadoOrden.CON_PESAJE_INICIAL) {
			throw new BusinessException("Error al cambiar el estado de orden. Es necesario que se encuentre en CON_PESAJE_INICIAL");
		}
		
		ordenEncontrada.get().setEstadoOrden(EstadoOrden.CERRADA_PARA_CARGA);
		ordenEncontrada.get().setPassword(null); // le sacamos la contrasenia.
		this.update(ordenEncontrada.get());
		return ordenEncontrada.get();
	}
    
}
