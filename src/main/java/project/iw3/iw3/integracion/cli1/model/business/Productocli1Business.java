package project.iw3.iw3.integracion.cli1.model.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.iw3.iw3.integracion.cli1.model.Productocli1;
import project.iw3.iw3.integracion.cli1.model.persistance.Productocli1Repository;
import project.iw3.iw3.integracion.cli1.model.business.interfaces.IProductocli1Business;
import project.iw3.iw3.model.Producto;
import project.iw3.iw3.model.business.exceptions.*;

import java.util.List;
import java.util.Optional;

@Service
public class Productocli1Business implements IProductocli1Business {

    @Autowired
    private Productocli1Repository productocli1Repository;

    @Override
    public Productocli1 load(String idCli1) throws NotFoundException, BusinessException {
        try {
            return productocli1Repository.findOneByIdCli1(idCli1)
                    .orElseThrow(() -> new NotFoundException("Producto CLI1 no encontrado: " + idCli1));
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }

    @Override
    public List<Productocli1> list() throws BusinessException {
        try {
            return productocli1Repository.findAll();
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }

    @Override
    @Transactional
    public Productocli1 add(Productocli1 producto) throws FoundException, BusinessException {
        try {
            Optional<Productocli1> existing = productocli1Repository.findOneByIdCli1(producto.getIdCli1());
            if (existing.isPresent())
                throw new FoundException("Ya existe producto con idCli1: " + producto.getIdCli1());
            return productocli1Repository.save(producto);
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }

    @Override
    @Transactional
    public Producto loadOrCreate(Productocli1 producto) throws BusinessException, NotFoundException {
        try {
            Optional<Productocli1> existing = productocli1Repository.findOneByIdCli1(producto.getIdCli1());
            if (existing.isPresent()) {
                return existing.get();
            } else {
                return productocli1Repository.save(producto);
            }
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }
}
