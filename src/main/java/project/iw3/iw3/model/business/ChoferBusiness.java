package project.iw3.iw3.model.business;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.micrometer.common.lang.Nullable;
import jakarta.transaction.Transactional;
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
    
    
    
	
	//objetivo --> crear o buscar un chofer
	@Override
	@Transactional //asegura que la operacion se haga en una transaccion de bd.
	
	public Chofer loadOrCreate(String dni, @Nullable String nombre, @Nullable String apellido) throws BusinessException {
		    // 1) Validacion basica
		
		    if (dni == null || dni.isBlank()) {
		        throw new BusinessException("Chofer: 'dni' es obligatorio.");
		    }
		
		    final String doc = dni.trim(); //no creo que haga falta pero por las dudas.
		
		    // 2) Intentamos buscar el chofer por dni
		    
		    Optional<Chofer> found = choferDAO.findByDni(doc); //Recordemos que el resultado de la bd se envuelve en un objeto optional.
		    // el proposito general es eliminar el riesgo de nullpointerexception.si no se encuentra el chofer, entonces optional estara vacio simplemente.
		    //optional.isPresent es verdadero si contiene un objeto.
		    
		    if (found.isPresent()) {
		        log.debug("Chofer existente recuperado: {}", found.get().getDni());
		        return found.get();
		    }
		
		    // 3) Si no existe, creamos uno nuevo
		    try {
		        Chofer nuevo = new Chofer();
		        nuevo.setDni(doc);
		        nuevo.setNombre(nombre != null ? nombre.trim() : "SIN_NOMBRE");
		        nuevo.setApellido(apellido != null ? apellido.trim() : "SIN_APELLIDO");
		
		        Chofer saved = choferDAO.save(nuevo);
		        log.info("Chofer creado: dni={}", saved.getDni());
		        return saved;
		
		    } catch (Exception e) {
		        log.error("Error creando chofer {}: {}", doc, e.getMessage(), e);
		        throw new BusinessException("Error creando chofer: " + e.getMessage(), e);
		    }
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
