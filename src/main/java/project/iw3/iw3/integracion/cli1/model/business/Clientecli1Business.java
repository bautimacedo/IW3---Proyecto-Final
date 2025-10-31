package project.iw3.iw3.integracion.cli1.model.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.iw3.iw3.integracion.cli1.model.Clientecli1;
import project.iw3.iw3.integracion.cli1.model.persistance.Clientecli1Repository;
import project.iw3.iw3.integracion.cli1.model.business.interfaces.IClientecli1Business;
import project.iw3.iw3.model.Cliente;
import project.iw3.iw3.model.business.exceptions.*;

import java.util.List;
import java.util.Optional;

@Service
public class Clientecli1Business implements IClientecli1Business {

    @Autowired
    private Clientecli1Repository clientecli1Repository;

    @Override
    public Clientecli1 load(String idCli1) throws NotFoundException, BusinessException {
        try {
            return clientecli1Repository.findOneByIdCli1(idCli1)
                    .orElseThrow(() -> new NotFoundException("Cliente CLI1 no encontrado: " + idCli1));
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }

    @Override
    public List<Clientecli1> list() throws BusinessException {
        try {
            return clientecli1Repository.findAll();
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }

    @Override
    @Transactional
    public Clientecli1 add(Clientecli1 cliente) throws FoundException, BusinessException {
        try {
            Optional<Clientecli1> existing = clientecli1Repository.findOneByIdCli1(cliente.getIdCli1());
            if (existing.isPresent())
                throw new FoundException("Ya existe cliente con idCli1: " + cliente.getIdCli1());
            return clientecli1Repository.save(cliente);
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }

   @Override
   @Transactional
    public Cliente loadOrCreate(Clientecli1 cliente) throws BusinessException, NotFoundException {
        try {
            Optional<Clientecli1> existing = clientecli1Repository.findOneByIdCli1(cliente.getIdCli1());

            if (existing.isPresent()) {
                // Como Clientecli1 extiende Cliente, devolvemos el mismo objeto
                return existing.get();
            } else {
                // Guardamos el nuevo cliente CLI1 y lo devolvemos (también es un Cliente)
                return clientecli1Repository.save(cliente);
            }
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }

}
