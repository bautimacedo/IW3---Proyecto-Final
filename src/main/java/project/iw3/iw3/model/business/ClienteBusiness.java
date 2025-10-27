package project.iw3.iw3.model.business;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import project.iw3.iw3.model.Cliente;
import project.iw3.iw3.model.business.exceptions.BusinessException;
import project.iw3.iw3.model.business.exceptions.FoundException;
import project.iw3.iw3.model.business.exceptions.NotFoundException;
import project.iw3.iw3.model.business.interfaces.IClienteBusiness;
import project.iw3.iw3.model.persistence.ClienteRepository;

@Service
@Slf4j
public class ClienteBusiness implements IClienteBusiness {

    @Autowired
    private ClienteRepository clienteRepository;

    @Override
     public List<Cliente> list() throws BusinessException {
        try {
            return clienteRepository.findAll();
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    public Cliente load(long id) throws NotFoundException, BusinessException {  //Cargar un cliente con id
        try{
            return clienteRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("No existe el cliente"));
        }catch(Exception e){
            throw new BusinessException(e.getMessage());
        }

    };

    @Override
    public Cliente load(String nombreEmpresa) throws NotFoundException,BusinessException{ //Carga un cliente con nombre
        try{
            return clienteRepository.findByNombreEmpresa(nombreEmpresa)
                    .orElseThrow(() -> new NotFoundException("No existe un cliente con esa Razon Social"));
        } catch(Exception e){
            throw new BusinessException();
        }

    }; 

    @Override
    public Cliente add(Cliente cliente) throws FoundException,BusinessException{ //Agregar cliente
        if (clienteRepository.findByNombreEmpresa(cliente.getNombreEmpresa()).isPresent()){
                throw new FoundException("Cliente con ese nombre ya existe");
            }
            return clienteRepository.save(cliente);
    }; 

    @Override
      public Cliente update(Cliente cliente) throws FoundException,NotFoundException,BusinessException{
        load(cliente.getId());
        if (clienteRepository.findByNombreEmpresaAndIdNot(cliente.getNombreEmpresa(), cliente.getId()).isPresent()) {
            throw new FoundException("Ya existe otro cliente con ese nombre");
        }
        return clienteRepository.save(cliente);
    }
    
      @Override
    public void delete(long id) throws NotFoundException, BusinessException {
        load(id);

        try {
            clienteRepository.deleteById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }
}
