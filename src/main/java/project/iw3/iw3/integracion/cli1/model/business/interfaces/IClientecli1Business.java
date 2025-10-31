package project.iw3.iw3.integracion.cli1.model.business.interfaces;

import project.iw3.iw3.integracion.cli1.model.Clientecli1;
import project.iw3.iw3.model.Cliente;
import project.iw3.iw3.model.business.exceptions.BusinessException;
import project.iw3.iw3.model.business.exceptions.FoundException;
import project.iw3.iw3.model.business.exceptions.NotFoundException;

import java.util.List;

public interface IClientecli1Business {

    Clientecli1 load(String idCli1) throws NotFoundException, BusinessException;

    List<Clientecli1> list() throws BusinessException;

    Clientecli1 add(Clientecli1 cliente) throws FoundException, BusinessException;

    Cliente loadOrCreate(Clientecli1 cliente) throws BusinessException, NotFoundException;
}
