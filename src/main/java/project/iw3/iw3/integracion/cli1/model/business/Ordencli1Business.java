package project.iw3.iw3.integracion.cli1.model.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.iw3.iw3.integracion.cli1.model.Ordencli1;
import project.iw3.iw3.integracion.cli1.model.persistance.Ordencli1Repository;
import project.iw3.iw3.integracion.cli1.model.business.interfaces.IOrdencli1Business;
import project.iw3.iw3.model.Orden;
import project.iw3.iw3.model.business.exceptions.*;

import java.util.List;
import java.util.Optional;

@Service
public class Ordencli1Business implements IOrdencli1Business {

    @Autowired
    private Ordencli1Repository ordencli1Repository;

    @Override
    public Ordencli1 load(String idCli1) throws NotFoundException, BusinessException {
        try {
            return ordencli1Repository.findOneByIdCli1(idCli1)
                    .orElseThrow(() -> new NotFoundException("Orden CLI1 no encontrada: " + idCli1));
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }

    @Override
    public List<Ordencli1> list() throws BusinessException {
        try {
            return ordencli1Repository.findAll();
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }

    @Override
    @Transactional
    public Ordencli1 add(Ordencli1 orden) throws FoundException, BusinessException {
        try {
            Optional<Ordencli1> existing = ordencli1Repository.findOneByIdCli1(orden.getNumeroOrdenCli1());
            if (existing.isPresent())
                throw new FoundException("Ya existe orden con idCli1: " + orden.getNumeroOrdenCli1());
            return ordencli1Repository.save(orden);
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }

    @Override
    @Transactional
    public Orden loadOrCreate(Ordencli1 orden) throws BusinessException, NotFoundException {
        try {
            Optional<Ordencli1> existing = ordencli1Repository.findOneByIdCli1(orden.getNumeroOrdenCli1());
            if (existing.isPresent()) {
                return existing.get();
            } else {
                return ordencli1Repository.save(orden);
            }
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }
}
