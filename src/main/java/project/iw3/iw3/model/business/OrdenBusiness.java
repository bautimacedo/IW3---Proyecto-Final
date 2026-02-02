package project.iw3.iw3.model.business;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import project.iw3.iw3.auth.IUserBusiness;
import project.iw3.iw3.auth.User;
import project.iw3.iw3.events.AlarmEvent;
import project.iw3.iw3.model.Alarm;
import project.iw3.iw3.model.Camion;
import project.iw3.iw3.model.ConciliacionDTO;
import project.iw3.iw3.model.DatosCargaDTO;
import project.iw3.iw3.model.DetalleCarga;
import project.iw3.iw3.model.Orden;
import project.iw3.iw3.model.business.exceptions.BusinessException;
import project.iw3.iw3.model.business.exceptions.FoundException;
import project.iw3.iw3.model.business.exceptions.NotFoundException;
import project.iw3.iw3.model.business.interfaces.IAlarmBusiness;
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
    private IUserBusiness userBusiness;

    @Autowired
    @Lazy
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
    private IAlarmBusiness alarmBusiness;


	@Autowired
    private OrdenRepository ordenRepository;
	
	@Autowired
    private ApplicationEventPublisher applicationEventPublisher;
	
	
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
	
	public Orden loadById(Long id) throws NotFoundException, BusinessException{
		Optional<Orden> o;
		try {
			o = ordenRepository.findById(id);
		}catch(Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).message(e.getMessage()).build();
		}
		if (o.isEmpty()) {
			throw NotFoundException.builder()
					.message("No se encuentra la orden con el id = " + id)
					.build();
		}
		return o.get();
		
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
	
	//PUNTO 1
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
		orden.setFechaPesajeTara(new java.util.Date());

		try {
			Orden actualizada = this.update(orden);
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
		
		orden.setEstadoOrden(EstadoOrden.CERRADA_PARA_CARGA);
		orden.setPassword(null); // le sacamos la contrasenia.
		orden.setFechaCierreCarga(new java.util.Date());
		orden.setUltimaFechaInformacion(new java.util.Date());
		this.update(orden);
		return orden;
		
	}
	
    
	// PUNTO 3)
	@Override
	public Orden recibirDatosCarga(DatosCargaDTO datos) throws BusinessException, NotFoundException {
	
		Orden orden = loadByNumeroOrden(datos.getNumeroOrden());
		
		if (datos.getPassword() == null || !Objects.equals(orden.getPassword(), datos.getPassword())) {
			throw BusinessException.builder()
            .message("Error en la contraseña. Deben coincidir")
            .build();
		}
		
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
	
	    
	    //validación alarma temperatura. si la temperatura de los datos es mayor a la temperatura umbral, entonces tiene que saltar la alarma.
	    if (datos.getTemperatura() > orden.getProducto().getTemperatura_umbral()) { // ==> temperatura umbarl
	    	if(! orden.isAlarmaActivada()) {//si la alarma no estaba activada
	    		orden.setAlarmaActivada(true);
	    		
	    		//lanzamos evento alarma.
	    		applicationEventPublisher.publishEvent(new AlarmEvent(datos, orden, AlarmEvent.TypeEvent.TEMPERATURA_SUPERADA));
	    	}
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

	        return this.update(orden);
	    } catch (Exception e) {
	        log.error(e.getMessage(), e);
	        throw BusinessException.builder()
	            .message("Error al actualizar datos de carga")
	            .ex(e).build();
	    }
	    
	}

	// PUNTO 5

	public ConciliacionDTO registrarPesajeFinal(Integer numeroOrden, Double pesoFinal) throws BusinessException, NotFoundException {

		if(numeroOrden == null || pesoFinal == null) {
			throw BusinessException.builder().message("El numero de orden y el peso final son obligatorios").build();
		}

		Optional<Orden> o;
		try {
			o = ordenRepository.findByNumeroOrden(numeroOrden);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).message(e.getMessage()).build();
		}

		if (o.isEmpty()) {
			throw NotFoundException.builder().message("No se encuentra la orden con numeroOrden=" + numeroOrden).build();
		}

		Orden orden = o.get();
		
		// validar estado: requerimos que la carga ya este cerrada (punto 4)
		if(orden.getEstadoOrden() != EstadoOrden.CERRADA_PARA_CARGA) {
			throw BusinessException.builder().message("La orden debe estar en estado CERRADA_PARA_CARGA").build();
		}

		// Calculamos: tara, producto cargado, promedios

		Double tara = (orden.getTara() != null) ? orden.getTara().doubleValue() : null;
		Double productoCargado = orden.getUltimaMasaAcumulada();

		// Obtenemos promedios desde detalle de carga
		List<DetalleCarga> detalles;
		try {
			detalles = detalleCargaRepository.findByOrdenId(orden.getId());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).message("Error al obtener detalles de carga").build();
		}

		double promedioTemp = orden.getPromedioTemperatura() != null ? orden.getPromedioTemperatura() : 0.0;
		double promedioDens = orden.getPromedioDensidad() != null ? orden.getPromedioDensidad() : 0.0;
		double promedioCaudal = orden.getPromedioCaudal() != null ? orden.getPromedioCaudal() : 0.0;

		if(!detalles.isEmpty()) {
			double sumTemp = 0.0, sumDens = 0.0, sumCaudal = 0.0;
			for (DetalleCarga det : detalles) {
				sumTemp += det.getTemperatura() != null ? det.getTemperatura() : 0.0;
				sumDens += det.getDensidad() != null ? det.getDensidad() : 0.0;
				sumCaudal += det.getCaudal() != null ? det.getCaudal() : 0.0;
			}
			int n = detalles.size();
			promedioTemp = sumTemp / n;
			promedioDens = sumDens / n;
			promedioCaudal = sumCaudal / n;
		}

		// Si no hay tara o producto cargado, devolvemos nulls coherentes
		Double netoPorBalanza = null;
		Double diferencia = null;
		
		if(tara != null) {
			netoPorBalanza = pesoFinal - tara;
			if(productoCargado != null) {
				diferencia = netoPorBalanza - productoCargado;
			}
		}

		Date fechaPesajeFinal = new java.util.Date();

		// presistir cambios en la orden
		orden.setPesoFinal(pesoFinal);
		// cambiamos estado a Finalizada
		orden.setEstadoOrden(EstadoOrden.FINALIZADA);
		orden.setFechaPesajeTara(orden.getFechaPesajeTara()); // mantenemos la fecha del pesaje inicial
		orden.setFechaCierreDeOrden(fechaPesajeFinal);
		orden.setPromedioCaudal(promedioCaudal);
		orden.setPromedioDensidad(promedioDens);
		orden.setPromedioTemperatura(promedioTemp);

		try {
			this.update(orden);
			//ordenRepository.save(orden);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).message("Error al actualizar orden con pesaje final").build();
		}

		ConciliacionDTO dto = new ConciliacionDTO();
		dto.setNumeroOrden(orden.getNumeroOrden());
		dto.setTara(tara);
		dto.setPesoFinal(pesoFinal);
		dto.setProductoCargado(productoCargado);
		dto.setNetoPorBalanza(netoPorBalanza);
		dto.setDiferencia(diferencia);
		dto.setPromedioTemperatura(promedioTemp);
		dto.setPromedioDensidad(promedioDens);
		dto.setPromedioCaudal(promedioCaudal);
		dto.setFechaPesajeFinal(fechaPesajeFinal);

		return dto;
	}

	// Punto 5: Devuelve la conciliacion para una orden que este en estado FINALIZADA
	@Override
	public ConciliacionDTO getConciliacion(Integer numeroOrden) throws NotFoundException, BusinessException {
		if(numeroOrden == null) {
			throw BusinessException.builder().message("El numero de orden es obligatorio").build();
		}

		Optional<Orden> o;
		try {
			o = ordenRepository.findByNumeroOrden(numeroOrden);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}

		if(o.isEmpty()) {
			throw NotFoundException.builder().message("No se encuentra la orden con numeroOrden=" + numeroOrden).build();
		}

		Orden orden = o.get();

		// validar estado finalizada
		if(orden.getEstadoOrden() != EstadoOrden.FINALIZADA) {
			throw BusinessException.builder().message("La orden debe estar en estado FINALIZADA").build();
		}

		// Reutilizamos la lógica de cálculo de conciliación del punto 5

		Double tara = orden.getTara() != null ? orden.getTara().doubleValue() : null;
		Double pesoFinal = orden.getPesoFinal();
		Double productoCargado = orden.getUltimaMasaAcumulada();
		List<DetalleCarga> detalles = detalleCargaRepository.findByOrdenId(orden.getId());

		double promedioTemp = orden.getPromedioTemperatura() != null ? orden.getPromedioTemperatura() : 0.0;
		double promedioDens = orden.getPromedioDensidad() != null ? orden.getPromedioDensidad() : 0.0;
		double promedioCaudal = orden.getPromedioCaudal() != null ? orden.getPromedioCaudal() : 0.0;
		if (!detalles.isEmpty()) {
			double sumTemp = 0.0, sumDens = 0.0, sumCaudal = 0.0;
			for (DetalleCarga det : detalles) {
				sumTemp += det.getTemperatura() != null ? det.getTemperatura() : 0.0;
				sumDens += det.getDensidad() != null ? det.getDensidad() : 0.0;
				sumCaudal += det.getCaudal() != null ? det.getCaudal() : 0.0;
			}
			int n = detalles.size();
			promedioTemp = sumTemp / n;
			promedioDens = sumDens / n;
			promedioCaudal = sumCaudal / n;
		}

		Double netoPorBalanza = null;
		Double diferencia = null;
		if (tara != null && pesoFinal != null) {
			netoPorBalanza = pesoFinal - tara;
			if (productoCargado != null) {
				diferencia = netoPorBalanza - productoCargado;
			}
		}

		ConciliacionDTO dto = new ConciliacionDTO(numeroOrden, tara, pesoFinal, productoCargado,
				netoPorBalanza, diferencia, promedioTemp, promedioDens, promedioCaudal, orden.getFechaCierreDeOrden());
		
		return dto;
	}
	
	
	// funcion para manejar las alarmas
	@Override
	public Orden alarmaAceptada(Long idAlarm, User user) throws NotFoundException, BusinessException {
		try {
			Alarm alarm = alarmBusiness.load(idAlarm);
			Orden orden = loadById(alarm.getOrden().getId());
			User userEncontrado = userBusiness.load(user.getUsername());
			if (!orden.isAlarmaActivada()) {
	            throw BusinessException.builder().message("La alarma ya fue aceptada").build();
	        }
	        if (orden.getEstadoOrden() != EstadoOrden.CON_PESAJE_INICIAL) {
	            throw BusinessException.builder().message("La orden no se encuentra en estado de carga").build();
	        }
	        alarm.setEstado((Alarm.Estado.ACEPTADA));
	        alarm.setUser(user);
	        alarmBusiness.update(alarm);
	        // la orden ya no tendria mas una alarma activada.
	        orden.setAlarmaActivada(false);
	        try {
	        	orden = update(orden);
	        }catch(Exception e) {
	        	throw BusinessException.builder().message("Error al actualizar la orden").build();
	        }
	        return orden;
		}catch(Exception e) {
			throw new BusinessException(e.getMessage(), e);
		}
	}

	
	public List<Orden> buscarOrdenesPorCamion(Camion camion) throws BusinessException {// Obtiene las órdenes por el camion
		
		try {
			return ordenRepository.findByCamion(camion);
		}catch(Exception e) {
			throw BusinessException.builder().message("Error interno en buscarOrdenesPorCamion").build();
		}
         
    }




}
