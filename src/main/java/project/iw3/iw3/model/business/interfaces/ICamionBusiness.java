package project.iw3.iw3.model.business.interfaces;

import java.util.List;

import project.iw3.iw3.model.Camion;
import project.iw3.iw3.model.business.exceptions.BusinessException;
import project.iw3.iw3.model.business.exceptions.FoundException;
import project.iw3.iw3.model.business.exceptions.NotFoundException;

public interface ICamionBusiness {

    List<Camion> list() throws BusinessException;

    Camion load(long id) throws NotFoundException, BusinessException;

    Camion load(String patente) throws NotFoundException, BusinessException;

    Camion add(Camion camion) throws FoundException, BusinessException;

    Camion update(Camion camion) throws FoundException, NotFoundException, BusinessException;

    void delete(long id) throws NotFoundException, BusinessException;
    
}
