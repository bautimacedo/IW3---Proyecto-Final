package project.iw3.iw3.integracion.cli1.model.business.interfaces;

import project.iw3.iw3.integracion.cli1.model.Cisternacli1;
import project.iw3.iw3.model.Cisterna;
import project.iw3.iw3.model.business.exceptions.*;

import java.util.List;

public interface ICisternacli1Business {

    Cisternacli1 load(String idCli1) throws NotFoundException, BusinessException;

    List<Cisternacli1> list() throws BusinessException;

    Cisternacli1 add(Cisternacli1 cisterna) throws FoundException, BusinessException;

    Cisterna loadOrCreate(Cisternacli1 cisterna) throws BusinessException, NotFoundException;
}
