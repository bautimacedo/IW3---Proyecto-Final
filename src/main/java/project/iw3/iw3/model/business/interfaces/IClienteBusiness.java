package project.iw3.iw3.model.business.interfaces;

import project.iw3.iw3.model.business.exceptions.*;
import java.util.List;
import project.iw3.iw3.model.Cliente;

public interface IClienteBusiness {  
    List<Cliente> list() throws BusinessException; //Listar clientes

    Cliente load(long id) throws NotFoundException,BusinessException; //Cargar un cliente con id

    Cliente load(String nombreEmpresa) throws NotFoundException,BusinessException; //Carga un cliente con nombre

    Cliente add(Cliente cliente) throws FoundException,BusinessException; //Agregar cliente

    public Cliente update(Cliente cliente) throws FoundException,NotFoundException,BusinessException; //Actualizar
}