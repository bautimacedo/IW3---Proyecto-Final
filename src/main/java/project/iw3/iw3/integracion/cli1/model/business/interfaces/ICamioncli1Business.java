package project.iw3.iw3.integracion.cli1.model.business.interfaces;

import project.iw3.iw3.integracion.cli1.model.Camioncli1;
import project.iw3.iw3.model.Camion;
import project.iw3.iw3.model.business.exceptions.BusinessException;
import project.iw3.iw3.model.business.exceptions.FoundException;
import project.iw3.iw3.model.business.exceptions.NotFoundException;

import java.util.List;

public interface ICamioncli1Business {

    Camioncli1 load(String idCli1) throws NotFoundException, BusinessException;

    List<Camioncli1> list() throws BusinessException;

    Camioncli1 add(Camioncli1 camion) throws FoundException, BusinessException;

    Camion loadOrCreate(Camioncli1 camion) throws BusinessException, NotFoundException;
}
