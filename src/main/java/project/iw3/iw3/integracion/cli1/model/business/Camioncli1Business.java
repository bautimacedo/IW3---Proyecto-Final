package project.iw3.iw3.integracion.cli1.model.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.iw3.iw3.integracion.cli1.model.Camioncli1;
import project.iw3.iw3.integracion.cli1.model.persistance.Camioncli1Repository;
import project.iw3.iw3.integracion.cli1.model.business.interfaces.ICamioncli1Business;
import project.iw3.iw3.model.Camion;
import project.iw3.iw3.model.business.exceptions.*;

import java.util.List;
import java.util.Optional;

@Service
public class Camioncli1Business implements ICamioncli1Business {

    @Autowired
    private Camioncli1Repository camioncli1Repository;

    @Override
    public Camioncli1 load(String idCli1) throws NotFoundException, BusinessException {
        try {
            return camioncli1Repository.findOneByIdCli1(idCli1)
                    .orElseThrow(() -> new NotFoundException("Camión CLI1 no encontrado: " + idCli1));
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }

    @Override
    public List<Camioncli1> list() throws BusinessException {
        try {
            return camioncli1Repository.findAll();
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }

    @Override
    @Transactional
    public Camioncli1 add(Camioncli1 camion) throws FoundException, BusinessException {
        try {
            Optional<Camioncli1> existing = camioncli1Repository.findOneByIdCli1(camion.getIdCli1());
            if (existing.isPresent())
                throw new FoundException("Ya existe camión con idCli1: " + camion.getIdCli1());
            return camioncli1Repository.save(camion);
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }

    @Override
    @Transactional
    public Camion loadOrCreate(Camioncli1 camion) throws BusinessException, NotFoundException {
        try {
            Optional<Camioncli1> existing = camioncli1Repository.findOneByIdCli1(camion.getIdCli1());
            if (existing.isPresent()) {
                return existing.get();
            } else {
                return camioncli1Repository.save(camion);
            }
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }
}
