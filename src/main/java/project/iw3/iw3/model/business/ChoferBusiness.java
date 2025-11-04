package project.iw3.iw3.model.business;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import project.iw3.iw3.model.Chofer;
import project.iw3.iw3.model.business.exceptions.BusinessException;
import project.iw3.iw3.model.business.exceptions.FoundException;
import project.iw3.iw3.model.business.exceptions.NotFoundException;
import project.iw3.iw3.model.business.interfaces.IChoferBusiness;
import project.iw3.iw3.model.persistence.ChoferRepository;

@Service
@Slf4j
public class ChoferBusiness implements IChoferBusiness {

    @Autowired
    private ChoferRepository choferDAO;

    @Override
    public Chofer add(Chofer chofer) throws FoundException, BusinessException {
       try{
        load(chofer.getId());
        throw FoundException.builder().message("Se encontro el Chofer id=" + chofer.getId()).build();
       } catch (NotFoundException e) {
            // Si no existe seguimos
       }

       try {
        load(chofer.getDni());
        throw FoundException.builder().message("Se encontro el chofer con numero de documento '"+ chofer.getDni()+"'").build();
       } catch (NotFoundException e) {
            // Si no existe seguimos
       }

       try {
            return choferDAO.save(chofer);
       }catch(Exception e) {
        log.error(e.getMessage(), e);
        throw BusinessException.builder().ex(e).build();
       }
    }
    
    @Override
	public Chofer loadOrCreate(Chofer chofer) throws BusinessException{
		if (chofer == null) {
	        throw BusinessException.builder().message("Chofer no puede ser null").build();
	    }
		
		final String dni = chofer.getDni() == null ? null : chofer.getDni().trim();
		
		if (dni == null || dni.isBlank()) {
	        throw BusinessException.builder()
	                .message("Se requiere documento para loadOrCreate")
	                .build();
	    }
		
		Chofer entity = null;
		
		//intentamos cargar x dni
		try {
			entity = this.add(chofer); 
			
		// si existe el chofer, entonces lo busca.
	    }catch(FoundException e){
	    	
	    	try {
	    		entity = this.load(dni);
	    	} catch(NotFoundException nf) {
	    		throw BusinessException.builder()
                .message("Estado inconsistente: Found por DNI pero luego no se pudo cargar: " + dni)
                .ex(nf)
                .build();
	    	}
	    	
	    } catch (BusinessException be) {
	        throw be; // re-propago
	    }
		
		return entity;
	}

    @Override
    public Chofer load(long id) throws NotFoundException, BusinessException {
        Optional<Chofer> r;
        try {
            r = choferDAO.findById(id);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            throw BusinessException.builder().ex(e).build();
        }
        if(r.isEmpty()){
            throw NotFoundException.builder().message("No se encuentra el Chofer id =" + id).build();
        }
        return r.get();
    }

    @Override
    public Chofer load(String dni) throws NotFoundException, BusinessException {
        Optional<Chofer> r;
        try {
            r = choferDAO.findByDni(dni);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            throw BusinessException.builder().ex(e).build();
        }
        if(r.isEmpty()){
            throw NotFoundException.builder().message("No se encuentra el Chofer con numero de documento'" + dni + "'").build();
        }
        return r.get();
    }
    
	
	
    @Override
    public List<Chofer> list() throws BusinessException {
        try {
            return choferDAO.findAll();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public Chofer update(Chofer chofer) throws FoundException, BusinessException, NotFoundException {
        
        load(chofer.getId());

        Optional<Chofer> dniExistente = null;
        try{
            dniExistente = choferDAO.findByDniAndIdNot(chofer.getDni(), chofer.getId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }

        if (dniExistente != null && dniExistente.isPresent()) {
            throw FoundException.builder().message("Se encontro otro Chofer con dni = " + chofer.getDni()).build();
        }

        try {
            return choferDAO.save(chofer);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public void delete(long id) throws NotFoundException, BusinessException {
        load(id);

        try {
            choferDAO.deleteById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }


    
}
