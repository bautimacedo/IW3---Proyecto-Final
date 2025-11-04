package project.iw3.iw3.model.business;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import project.iw3.iw3.model.Camion;
import project.iw3.iw3.model.Chofer;
import project.iw3.iw3.model.Cisterna;
import project.iw3.iw3.model.business.exceptions.BusinessException;
import project.iw3.iw3.model.business.exceptions.FoundException;
import project.iw3.iw3.model.business.exceptions.NotFoundException;
import project.iw3.iw3.model.business.interfaces.ICamionBusiness;
import project.iw3.iw3.model.persistence.CamionRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CamionBusiness implements ICamionBusiness {

    @Autowired
    private CamionRepository camionRepository;

    @Override
    public List<Camion> list() throws BusinessException {
        try {
            return camionRepository.findAll();
        } catch (Exception e) {
            throw new BusinessException(e.getMessage(), e);
        }
    }

    @Override
    public Camion load(long id) throws NotFoundException, BusinessException {
        try {
            return camionRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("No existe el camión con id " + id));
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(e.getMessage(), e);
        }
    }

    @Override
    public Camion load(String patente) throws NotFoundException, BusinessException {
        try {
            return camionRepository.findByPatente(patente)
                    .orElseThrow(() -> new NotFoundException("No existe el camión con la patente " + patente));
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(e.getMessage(), e);
        }
    }

    @Override
    public Camion add(Camion camion) throws FoundException, BusinessException {
        try {
            // Validar duplicado por patente
            if (camionRepository.findByPatente(camion.getPatente()).isPresent()) {
                throw new FoundException("Ya existe un camión con esa patente");
            }
            return camionRepository.save(camion);
        } catch (FoundException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(e.getMessage(), e);
        }
    }

    @Override
    public Camion update(Camion camion) throws FoundException, NotFoundException, BusinessException {
        try {
            // Validar existencia
            load(camion.getId());

            // Validar duplicado en OTRO camión
            if (camionRepository.findByPatenteAndIdNot(camion.getPatente(), camion.getId()).isPresent()) {
                throw new FoundException("Ya existe otro camión con esa patente");
            }

            return camionRepository.save(camion);
        } catch (FoundException | NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(e.getMessage(), e);
        }
    }

      @Override
    public void delete(long id) throws NotFoundException, BusinessException {
        load(id);

        try {
            camionRepository.deleteById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

	  @Override
	  public Camion loadOrCreate(Camion camion) throws BusinessException, NotFoundException {
		 
		  if (camion == null) {
			  throw BusinessException.builder().message("Camion no puede ser null").build();
		  }
		  
		  Camion entity = null;
		  
		  try {
			  
			  entity = this.load(camion.getPatente());
			  
		  } catch(NotFoundException ignore) {
			  // si no lo encontramos lo creamos
			  try {
				  entity =  this.add(camion);
			  } catch (FoundException ignored) {
				  // esto es imposible que pase
			  }
		  }
		  
		  return entity;
		  
		  
		  
		  // puede pasar que el camion ya existia, pero viene con distintas cisternas. en ese caso, tenemos que agregarlas
		  /*
		  for (Cisterna cisterna : camion.getCisterna()) { // aca obtenemos del camion que viene toda la lista de cisternas y las recorremos una por una con cisterna
			  try {
				  cisterna.setCamion(camion.get());
			  }
		  }
		  */
		  
	  }
   
}    