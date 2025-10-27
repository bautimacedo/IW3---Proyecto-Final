package project.iw3.iw3.model.business.interfaces;

import project.iw3.iw3.model.Cisterna;
import project.iw3.iw3.model.business.exceptions.BusinessException;
import project.iw3.iw3.model.business.exceptions.FoundException;
import project.iw3.iw3.model.business.exceptions.NotFoundException;

import java.util.List;

public interface ICisternaBusiness {

    List<Cisterna> list() throws BusinessException;

    Cisterna load(long id) throws NotFoundException, BusinessException;

    Cisterna load(String codigo) throws NotFoundException, BusinessException;

    Cisterna add(Cisterna cisterna) throws FoundException, BusinessException;

    Cisterna update(Cisterna cisterna) throws FoundException, NotFoundException, BusinessException;

    void delete(long id) throws NotFoundException, BusinessException;
}
