package project.iw3.iw3.model.business;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import project.iw3.iw3.model.DatosCargaDTO;
import project.iw3.iw3.model.DetalleCarga;
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
import project.iw3.iw3.model.persistence.DetalleCargaRepository;
import project.iw3.iw3.model.persistence.OrdenRepository;
import project.iw3.iw3.util.GeneradorDePasswordActivacionPaso2;
import project.iw3.iw3.util.JsonUtiles;
import project.iw3.iw3.model.OrdenJsonDeserializer;


@Service
@Slf4j
public class OrdenBusiness implements IOrdenBusiness {

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
	
	
	@Autowired
	private DetalleCargaRepository detalleCargaRepository;


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
	public Orden load(Integer numeroOrden) throws NotFoundException, BusinessException { //cargar una orden por numero de orden
		
		if(numeroOrden == null) {
			throw NotFoundException.builder().message("El numero de orden es obligatorio").build();
		}
		
		Optional<Orden> o;
		try {
			o = ordenRepository.findByNumeroOrden(numeroOrden);
		}catch(Exception e){
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).message(e.getMessage()).build();
		}
		if(o.isEmpty()) {
			throw NotFoundException.builder().message("No se encuentra la orden con numero de orden=" + numeroOrden).build();
		}
		return o.get();
		
	}

	@Override
	public Orden update(Orden orden) throws FoundException, NotFoundException, BusinessException {
		if (orden.getNumeroOrden() == null) {
			throw NotFoundException.builder().message("El numero de orden es obligatorio").build();
		}

		// 1) buscamos la orden existente (lanza NotFoundException si no está)
		Orden existente = load(orden.getNumeroOrden());

		// 2) forzamos el id de BD en el objeto recibido para que save() haga update
		orden.setId(existente.getId());

		try {
			return ordenRepository.save(orden);
		} catch (org.springframework.dao.DataIntegrityViolationException dive) {
			// por si la DB rechaza unique (condición de carrera u otra violación)
			log.warn("Violación integridad al actualizar orden numeroOrden={}: {}", orden.getNumeroOrden(),
					dive.getMessage());
			throw FoundException.builder().message("Ya existe una orden con numeroOrden=" + orden.getNumeroOrden())
					.ex(dive).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().message("Error al Actualizar Orden").ex(e).build();
		}
	}

	@Override
	public void delete(Integer numeroOrden) throws NotFoundException, BusinessException {
		Orden orden = load(numeroOrden);
		try {
			ordenRepository.delete(orden);
		}catch(Exception e) {
			log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
		}
		
	}

	@Override
	public Orden add(Orden orden) throws FoundException, BusinessException {
		
		if(orden.getNumeroOrden() != null) {
			
		
			try {
				load(orden.getNumeroOrden());
				throw FoundException.builder().message("Se encuentró el numero de orden =" + orden.getNumeroOrden()).build();
			}catch(NotFoundException e) {
			
			}
			if (orden.getPesoFinal() == null) {
    			orden.setPesoFinal(0.0);
			}

		}
		try {
			return ordenRepository.save(orden);
		}catch(Exception e) {
			log.error(e.getMessage(), e);
			 throw BusinessException.builder().message("Error al Crear Nueva Orden").build();
		}
	}
	
	

	
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
        	// Después de configurar el ObjectMapper (mapper) con nuestro deserializador personalizado, esta línea ejecuta el proceso:
        	// recibe la cadena json y Orden.class: Le dice a Jackson: "Convertila en un objeto de tipo Orden."
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
		Orden orden = ordenEncontrada.get();

		orden.setPassword(password);
		orden.setTara(tara);
		orden.setEstadoOrden(EstadoOrden.CON_PESAJE_INICIAL);

		try {
			Orden actualizada = ordenRepository.save(orden);
			log.info("Orden {} actualizada a estado {} con password {}", 
					actualizada.getNumeroOrden(), actualizada.getEstadoOrden(), actualizada.getPassword());
			return actualizada;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().message("Error al actualizar orden").ex(e).build();
		}

		

	}

	//PUNTO4
	@Override
	public Orden cerrarOrden(Integer numeroOrden) throws BusinessException, NotFoundException, FoundException {
		
		Optional<Orden> ordenEncontrada;
		
		try {
			ordenEncontrada = ordenRepository.findByNumeroOrden(numeroOrden);
		}catch(Exception e) {
			log.error(e.getMessage(), e);
            throw new BusinessException("Error al encontrar la orden por ID", e);
		} if (ordenEncontrada.isEmpty()) {
            throw new NotFoundException("No se ha encontrado la orden");
        }

		Orden orden = ordenEncontrada.get();

		if(orden.getEstadoOrden() != EstadoOrden.CON_PESAJE_INICIAL) {
			throw new BusinessException("Error al cambiar el estado de orden. Es necesario que se encuentre en CON_PESAJE_INICIAL");
		}
		
		List<DetalleCarga> detalles = detalleCargaRepository.findByOrdenId(orden.getId());

		if (detalles.isEmpty()) {
			throw new BusinessException("No se pueden cerrar órdenes sin datos de carga.");
		}

		double promedioDensidad = 0;
		double promedioTemperatura = 0;
		double promedioCaudal = 0;

		for (DetalleCarga detalle : detalles) {
			promedioDensidad += detalle.getDensidad();
			promedioTemperatura += detalle.getTemperatura();
			promedioCaudal += detalle.getCaudal();
		}

		orden.setPromedioDensidad(promedioDensidad / detalles.size());
		orden.setPromedioTemperatura(promedioTemperatura / detalles.size());
		orden.setPromedioCaudal(promedioCaudal / detalles.size());

		orden.setUltimaFechaInformacion(new java.util.Date());
		orden.setFechaCierreCarga(new java.util.Date());
		orden.setPesoFinal(orden.getUltimaMasaAcumulada());
		orden.setEstadoOrden(EstadoOrden.CERRADA_PARA_CARGA);
		orden.setPassword(null); // le sacamos la contrasenia.
		this.update(orden);
		return orden;
	}
    
	// PUNTO 3)
	@Override
	public Orden recibirDatosCarga(DatosCargaDTO datos) throws BusinessException, NotFoundException {
	
		Orden orden = load(datos.getOrderId());
	
	
	    // Verificar estado
	    if (orden.getEstadoOrden() != EstadoOrden.CON_PESAJE_INICIAL) {
	        throw BusinessException.builder()
	                .message("La orden no está habilitada para recibir datos de carga (estado inválido)")
	                .build();
	    }
	
	    // Validaciones simples
	    if (datos.getMasa() == null || datos.getDensidad() == null ||
	        datos.getTemperatura() == null || datos.getCaudal() == null) {
	        throw BusinessException.builder().message("Datos incompletos en la carga").build();
	    }
	
	    if (datos.getCaudal() < 0) {
	        throw BusinessException.builder().message("Caudal inválido (menor a 0)").build();
	    }
	
	    if (orden.getUltimaMasaAcumulada() != null &&
	        datos.getMasa() < orden.getUltimaMasaAcumulada()) {
	        throw BusinessException.builder().message("Masa acumulada retrocedió, dato inválido").build();
	    }
	
	    //  Si es el primer dato de carga, registrar fecha de inicio
	    if (orden.getFechaInicioCarga() == null) {
	        orden.setFechaInicioCarga(new java.util.Date());
	        log.info("Orden {} - Fecha de inicio de carga registrada: {}", orden.getNumeroOrden(), orden.getFechaInicioCarga());
	    }
	
	    // Actualizar datos en cabecera
	    orden.setUltimaFechaInformacion(new java.util.Date());
	    orden.setUltimaMasaAcumulada(datos.getMasa());
	    orden.setUltimaDensidad(datos.getDensidad());
	    orden.setUltimaTemperatura(datos.getTemperatura());
	    orden.setUltimaFlowRate(datos.getCaudal());
	
	    try {
	        // Guardamos en la tabla de detalle
	        DetalleCarga detalle = new DetalleCarga();
	        detalle.setOrden(orden);
	        detalle.setMasaAcumulada(datos.getMasa());
	        detalle.setDensidad(datos.getDensidad());
	        detalle.setTemperatura(datos.getTemperatura());
	        detalle.setCaudal(datos.getCaudal());
	        detalle.setEstampaTiempo(new java.util.Date());

	        detalleCargaRepository.save(detalle); //  inserta el registro histórico

	        return ordenRepository.save(orden);
	    } catch (Exception e) {
	        log.error(e.getMessage(), e);
	        throw BusinessException.builder()
	            .message("Error al actualizar datos de carga")
	            .ex(e).build();
	    }
	    
	}




	@Override
	public Orden loadByNumeroOrden(Integer numeroOrden) throws NotFoundException, BusinessException {
		Optional<Orden> o;
		try {
			o = ordenRepository.findByNumeroOrden(numeroOrden);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).message(e.getMessage()).build();
		}
		if (o.isEmpty()) {
			throw NotFoundException.builder()
					.message("No se encuentra la orden con numeroOrden=" + numeroOrden)
					.build();
		}
		return o.get();
	}



}
