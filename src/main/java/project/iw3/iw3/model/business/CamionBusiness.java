package project.iw3.iw3.model.business;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import io.micrometer.common.lang.Nullable;
import jakarta.transaction.Transactional;
import project.iw3.iw3.model.Camion;
import project.iw3.iw3.model.Cisterna;
import project.iw3.iw3.model.Orden;
import project.iw3.iw3.model.business.exceptions.BusinessException;
import project.iw3.iw3.model.business.exceptions.FoundException;
import project.iw3.iw3.model.business.exceptions.NotFoundException;
import project.iw3.iw3.model.persistence.CamionRepository;
import project.iw3.iw3.model.business.interfaces.*;
import project.iw3.iw3.model.enums.EstadoOrden;
import project.iw3.iw3.util.*;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CamionBusiness implements ICamionBusiness {
	
	@Autowired
    private CamionRepository camionDAO;

    @Autowired
    private ICisternaBusiness cisternaBusiness;
    
    @Autowired
    @Lazy
    private IOrdenBusiness ordenBusiness;

    @Override
    public List<Camion> list() throws BusinessException {
        try {
            return camionDAO.findAll();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public Camion load(long id) throws NotFoundException, BusinessException {
        Optional<Camion> r;
        try {
            r = camionDAO.findById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }

        if (r.isEmpty()) {
            throw NotFoundException.builder()
                    .message("No se encuentra el Camión id = " + id)
                    .build();
        }
        return r.get();
    }

    @Override
    public Camion load(String patente) throws NotFoundException, BusinessException {
        Optional<Camion> r;
        try {
            r = camionDAO.findByPatente(patente);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }

        if (r.isEmpty()) {
            throw NotFoundException.builder()
                    .message("No se encuentra el Camión con patente = '" + patente + "'")
                    .build();
        }
        return r.get();
    }

    @Override
    public Camion add(Camion camion) throws FoundException, BusinessException {
        // Validar existencia previa
        try {
            load(camion.getPatente());
            throw FoundException.builder()
                    .message("Ya existe un Camión con la patente '" + camion.getPatente() + "'")
                    .build();
        } catch (NotFoundException e) {
            // No existe, seguimos
        }

        try {
            return camionDAO.save(camion);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public Camion update(Camion camion) throws FoundException, NotFoundException, BusinessException {
        // Debe existir el ID
        load(camion.getId());

        // Validar duplicado de patente en otro Camión
        Optional<Camion> patenteExistente;
        try {
            patenteExistente = camionDAO.findByPatenteAndIdNot(camion.getPatente(), camion.getId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }

        if (patenteExistente != null && patenteExistente.isPresent()) {
            throw FoundException.builder()
                    .message("Se encontró otro Camión con patente = " + camion.getPatente())
                    .build();
        }

        try {
            return camionDAO.save(camion);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public void delete(long id) throws NotFoundException, BusinessException {
        load(id);

        try {
            camionDAO.deleteById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

	@Override
	@Transactional 
	public Camion loadOrCreate(String patente, @Nullable String descripcion, @Nullable JsonNode cisternasNode) throws BusinessException {
		
		    //1. Validar datos básicos
		    if (patente == null || patente.isBlank()) {
		        throw new BusinessException("Camión: 'patente' es obligatoria.");
		    }
		
		    final String pat = patente.trim().toUpperCase();
		
		    // 2. intentar buscar el camion existente
		    
		    Optional<Camion> found = camionDAO.findByPatente(pat);
		    
		    // 3. verificar si vienen cisternas en el body 
		    
		    boolean cisternas_body = cisternasNode != null && cisternasNode.isArray();
		    
		   // si existe el camion: 
		    if (found.isPresent()) {
		        log.debug("Camion existente recuperado: {}", found.get().getPatente());
		        
		        List<Orden> ordenes  = ordenBusiness.buscarOrdenesPorCamion(found.get());
		          	
	        	if (ordenes.isEmpty()) { 
	        	// si viene vacio entonces este camion existe y no esta asociado a ninguna orden
	        	//Significa que no esta asociado a ninguna orden.
	        		log.debug("EL CAMION NO esta en ninguna orden{}", found.get().getPatente());
	        	
	        	} else {// si ordenes no está empty (osea que este camion esta registrado en la bd con alguna orden) vamos a verificar que esas ordenes no esten en un estado que no sea finalizado.
	        		
	        		for (Orden orden : ordenes) {
						if (orden.getEstadoOrden() != EstadoOrden.FINALIZADA) {
							log.debug("El camion con patente " + found.get().getPatente() + "esta asociada a la orden " + orden.getId() + 
									"cuyo estado es " + orden.getEstadoOrden() + "distinta a FINALIZADO");
							
							throw new BusinessException("El camion con patente " + found.get().getPatente() + "esta asociada a la orden " + orden.getId() + 
									"cuyo estado es " + orden.getEstadoOrden() + "distinta a FINALIZADO");
						}
					}
	        	
	        	} 
		        //modificacion si vienen nuevas cisternas
		        if(cisternas_body) {
		        	try{
		        		
		        		Camion updatedCamion = updateCisternas(found.get(), cisternasNode);
		        		return updatedCamion;
		        		
		        	}catch (Exception e) {
		        		log.error("Error actualizando cisternas en camion {}: {}", pat, e.getMessage(), e);
		 		        throw new BusinessException("Error actualizando cisternas en camion " + e.getMessage(), e);
		        	}
		        	
		        	
		        }else {
		        	
		        	
		        	if(found.get().getCisterna().isEmpty()) {// si no tiene ninguna cisterna asociada
	        			log.error("El camion no pertenece a ninguna orden pero no tiene asociada ninguna cisterna");
		 		        throw new BusinessException("El camion no pertenece a ninguna orden pero no tiene asociada ninguna cisterna");
		        	}else {
		        		return found.get();
		        	}
		        	
		        }
		        
		        
		    }
		
		    // 3. Si no existe, creamos uno nuevo
		    try {
		    	
		        Camion nuevo = new Camion();
		        nuevo.setPatente(pat);
		        nuevo.setDescripcion(descripcion); // puede ser null
		        
		        // Persistir primero para obtener ID (necesario si crearCisternaDesdeJson llama moverCisternaACamion)
		        nuevo = camionDAO.save(nuevo);
		        
			        //procesamiento de las cisternas del json
			        if (cisternas_body) {
			        	
			        	Set<Cisterna> nuevasCisternas = new HashSet<>(); //estructura de datos que almacena datos unicos.
			        	
			        	//iteramos sobre la lista de cisternas que llega en el json
			        	for (JsonNode cisternaNode : cisternasNode) {
			        		
			        		Cisterna nuevaCisterna = crearCisternaDesdeJson(cisternaNode, nuevo); //nuevo ya tiene ID
			        		nuevasCisternas.add(nuevaCisterna); //la agregamos al hashset
			        	}
			        	
			        	nuevo.setCisterna(nuevasCisternas);
			        }
		        
		        Camion saved = camionDAO.save(nuevo);
		        log.info("Camion creado con {} cisternas: patente={}", saved.getCisterna().size(), saved.getPatente());
		        return saved;
		       
		    } catch (Exception e) {
		    	
		        log.error("Error creando camión {}: {}", pat, e.getMessage(), e);
		        throw new BusinessException("Error creando camion: " + e.getMessage(), e);
		        
		    }
	}
	
	private Cisterna moverCisternaACamion(Cisterna cisterna, Camion camionDestino) throws BusinessException {
	    try {
	        // Paso 1: Eliminar la cisterna del camión origen
	        Camion camionOrigen = cisterna.getCamion(); // Obtener el camión actual de la cisterna
	        camionOrigen.getCisterna().remove(cisterna); // Eliminar la cisterna de la lista del camión origen
	        cisterna.setCamion(camionDestino); // Asignar la nueva relación con el camión destino
	        camionDestino.getCisterna().add(cisterna); // Agregar la cisterna a la lista de cisternas del camión destino

	        // Paso 2: Actualizar la cisterna
	        return cisternaBusiness.update(cisterna); // Guardar la cisterna con el nuevo camión

	    } catch (Exception e) {
	        throw new BusinessException("Error al mover la cisterna", e);
	    }
	}
	
	//clase que no tiene mucho que ver aca pero la dejamos por necesidad. Si queremos despues la cambiamos.
	//aca falta verificar si la cisterna existe o no en la BD.
	private Cisterna crearCisternaDesdeJson (JsonNode cisternaNode, Camion camion) throws BusinessException {
		
		long capacidad = JsonUtiles.getLong(cisternaNode, ConstantesJson.CISTERNA_CAPACIDAD_LITROS_ATTRIBUTES, 0);
		String licencia = JsonUtiles.getString(cisternaNode, ConstantesJson.CISTERA_LICENCIA_ATTRIBUTES, null);
		
		
		if (licencia == null || licencia.isBlank()) {
			throw new BusinessException("La cisterna del camión debe tener 'licence_plate'.");
		}
		
		try {
			Cisterna existe = cisternaBusiness.load(licencia);
			
			
			//la cisterna Existe y no pertenece al camion con el que vamos a geenrar la orden. ==> cisterna asignada a otro mionca.
			if(existe != null && existe.getCamion() != null && existe.getCamion().getId() != camion.getId()) {
				
				Camion camionCisterna = existe.getCamion();
				
				List<Orden> ordenes = ordenBusiness.buscarOrdenesPorCamion(camionCisterna);
				
				if (!ordenes.isEmpty()) {
					for (Orden orden : ordenes) {
						if (orden.getEstadoOrden() != EstadoOrden.FINALIZADA) {
							throw new BusinessException("La cisterna con licencia " + licencia + " ya está asignada al camión: " + existe.getCamion().getPatente() + 
									"Y este camion esta asignado a la Orden no FINALIZADA Id = " + orden.getId());
						}
					}
					//Aca entonces estamos en la situacion en que la cisterna existe, esta asociada a un camion, el camion esta asociado a una orden pero esta finalizada.
					try {
						existe = moverCisternaACamion(existe, camion);
					}catch(Exception e) {
						 throw new BusinessException(e.getMessage(), e);
					}
				}else {
					//Aca estamos en el caso de que el camion no esta asociado a ninguna orden
					try {
						existe = moverCisternaACamion(existe, camion);
					}catch(Exception e) {
						 throw new BusinessException(e.getMessage(), e);
					}
				}
				
				
			}			
			return (existe);
			
			
		}catch(NotFoundException e) {
			
			// La Cisterna no existe: Crear una nueva
	        Cisterna c = new Cisterna();
	        c.setCapacidadLitros(capacidad);
	        c.setLicencia(licencia);
	        c.setCamion(camion);
	        return c; 
	        
		}catch(Exception e) {
	        
	        throw new BusinessException("Falla al procesar cisterna con licencia " + licencia + ": " + e.getMessage(), e);
		}
		
	}
	
	
	private Camion updateCisternas (Camion camion, JsonNode cisternasNode) throws BusinessException {
		
		
		// 1. convertir una lista de objetos Cisterna (asociados a un objeto Camion) en un Map en el que la clave es el valor de Licencia de cada Cisterna y el valor es el propio objeto Cisterna.
	    Map<String, Cisterna> cisternasActuales = camion.getCisterna().stream()
	            .collect(Collectors.toMap(Cisterna::getLicencia, Function.identity()));
	    
	    // 2. Crear un nuevo Set con las Cisternas que deben existir después de la actualización
	    Set<Cisterna> cisternasActualizadas = new HashSet<>();
		
		
		for (JsonNode cisternaNode : cisternasNode) {
			
			 Cisterna cisternaFromJson = crearCisternaDesdeJson(cisternaNode, camion);
		     /*String licencia = cisternaFromJson.getLicencia();
		     
		       
		     if (cisternasActuales.containsKey(licencia)) {
		            // A) ACTUALIZACIÓN: La cisterna YA EXISTE en el Camion.
		            Cisterna existente = cisternasActuales.get(licencia);
		            
		            // Si la encontraste en el set, asumes que tiene el ID (es la misma en BD)
		            // Solo actualizas los campos mutables que vinieron en el JSON (ej. Capacidad)
		            existente.setCapacidadLitros(cisternaFromJson.getCapacidadLitros());
		            
		            // La mantenemos para el set final y la quitamos del mapa de "borrables"
		            cisternasActualizadas.add(existente);
		            cisternasActuales.remove(licencia); // La remueve para que no se borre al final
		            
		        } else {
		            // B) INSERCIÓN/ASIGNACIÓN: La cisterna es NUEVA para este camión (ya sea que exista en BD sin asignación o sea nueva)
		            // La entidad `cisternaFromJson` viene ya construida y ligada al camión actual por `crearCisternaDesdeJson`.
		            cisternasActualizadas.add(cisternaFromJson);*/
		      cisternasActualizadas.add(cisternaFromJson);
		}
		// 3. SINCRONIZACIÓN
	    
	    // Las Cisternas que quedan en 'cisternasActuales' son las que estaban en BD y NO vinieron en el JSON.
	    // Usamos clear/addAll para que JPA maneje el ORPHAN REMOVAL (borrado de las no incluidas)
	    
	    // 3.1 Desasigna las cisternas que se van a eliminar para evitar errores de restricción foreign key si la eliminacion falla.
	   /*  for(Cisterna c : cisternasActuales.values()){
	        // Si quieres que la cisterna se elimine de la BD:
	        // NO HACER NADA, el clear() y el orphanRemoval=true se encargan.
	        
	        // Si quieres que la cisterna se quede en la BD pero se desasigne del Camión:
	        // c.setCamion(null); // Desasigna
	    }*/
	    
	    camion.getCisterna().clear();           // JPA marca todas las entidades existentes para eliminación
	    camion.getCisterna().addAll(cisternasActualizadas); // JPA marca las nuevas para inserción/actualización

	    // El return es opcional ya que es @Transactional, pero ayuda a la claridad:
	    return camionDAO.save(camion); 
	}
	
   
}    