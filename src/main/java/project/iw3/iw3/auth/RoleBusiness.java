package project.iw3.iw3.auth;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import project.iw3.iw3.model.business.exceptions.BusinessException;
import project.iw3.iw3.model.business.exceptions.NotFoundException;

@Service
@Slf4j
public class RoleBusiness implements IRoleBusiness {

	@Autowired
    private RoleRepository roleDAO;
	
	
	@Override
    public Role add(Role role) throws BusinessException {
        try {
            return roleDAO.save(role);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public Role update(Role role) throws NotFoundException, BusinessException {
        // Verificamos que exista antes de intentar modificarlo
        load(role.getName()); 
        try {
            return roleDAO.save(role);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public void delete(int id) throws NotFoundException, BusinessException {
        // 1. Verificamos si existe antes de borrar
        if (!roleDAO.existsById(id)) {
            throw NotFoundException.builder()
                    .message("No existe el rol con id " + id)
                    .build();
        }
        
        try {
            roleDAO.deleteById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public List<Role> list() throws BusinessException {
        try {
            return roleDAO.findAll();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public Role load(String name) throws NotFoundException, BusinessException {
        Optional<Role> or = roleDAO.findByName(name);
        if (or.isEmpty()) {
            throw NotFoundException.builder().message("No se encuentra el rol: " + name).build();
        }
        return or.get();
    }
    
    
    
}


