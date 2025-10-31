package project.iw3.iw3.integracion.cli1.model.business.interfaces;

import project.iw3.iw3.integracion.cli1.model.Productocli1;
import project.iw3.iw3.model.Producto;
import project.iw3.iw3.model.business.exceptions.*;

import java.util.List;

public interface IProductocli1Business {

    Productocli1 load(String idCli1) throws NotFoundException, BusinessException;

    List<Productocli1> list() throws BusinessException;

    Productocli1 add(Productocli1 producto) throws FoundException, BusinessException;

    Producto loadOrCreate(Productocli1 producto) throws BusinessException, NotFoundException;
}
