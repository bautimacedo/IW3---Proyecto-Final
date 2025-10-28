package project.iw3.iw3.model.business.interfaces;

import java.util.List;

import project.iw3.iw3.model.Chofer;
import project.iw3.iw3.model.business.exceptions.BusinessException;
import project.iw3.iw3.model.business.exceptions.FoundException;
import project.iw3.iw3.model.business.exceptions.NotFoundException;

public interface IChoferBusiness {
    
    public Chofer add(Chofer chofer) throws FoundException, BusinessException;

    public Chofer load(long id) throws NotFoundException, BusinessException;

    public Chofer load(String dni) throws NotFoundException, BusinessException;

    public List<Chofer> list() throws BusinessException;

    public Chofer update(Chofer chofer) throws FoundException, BusinessException, NotFoundException;

    public void delete(long id) throws NotFoundException, BusinessException;
}
