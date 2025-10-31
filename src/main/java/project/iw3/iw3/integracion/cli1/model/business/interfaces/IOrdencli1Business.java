package project.iw3.iw3.integracion.cli1.model.business.interfaces;

import project.iw3.iw3.integracion.cli1.model.Ordencli1;
import project.iw3.iw3.model.Orden;
import project.iw3.iw3.model.business.exceptions.*;

import java.util.List;

public interface IOrdencli1Business {

    Ordencli1 load(String idCli1) throws NotFoundException, BusinessException;

    List<Ordencli1> list() throws BusinessException;

    Ordencli1 add(Ordencli1 orden) throws FoundException, BusinessException;

    Orden loadOrCreate(Ordencli1 orden) throws BusinessException, NotFoundException;
}
