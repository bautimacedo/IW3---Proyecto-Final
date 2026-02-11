package project.iw3.iw3.auth;

import java.util.List;

import project.iw3.iw3.model.business.exceptions.BusinessException;
import project.iw3.iw3.model.business.exceptions.NotFoundException;

public interface IRoleBusiness {
	
	Role add(Role role) throws BusinessException;
    Role update(Role role) throws NotFoundException, BusinessException;
    void delete(int id) throws NotFoundException, BusinessException;
    List<Role> list() throws BusinessException;
    Role load(String name) throws NotFoundException, BusinessException;
    
}

