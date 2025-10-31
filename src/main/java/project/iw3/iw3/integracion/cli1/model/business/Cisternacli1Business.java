package project.iw3.iw3.integracion.cli1.model.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.iw3.iw3.integracion.cli1.model.Cisternacli1;
import project.iw3.iw3.integracion.cli1.model.persistance.Cisternacli1Repository;
import project.iw3.iw3.integracion.cli1.model.business.interfaces.ICisternacli1Business;
import project.iw3.iw3.model.Cisterna;
import project.iw3.iw3.model.business.exceptions.*;

import java.util.List;
import java.util.Optional;

@Service
public class Cisternacli1Business implements ICisternacli1Business {

    @Autowired
    private Cisternacli1Repository cisternacli1Repository;

    @Override
    public Cisternacli1 load(String idCli1) throws NotFoundException, BusinessException {
        try {
            return cisternacli1Repository.findOneByIdCli1(idCli1)
                    .orElseThrow(() -> new NotFoundException("Cisterna CLI1 no encontrada: " + idCli1));
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }

    @Override
    public List<Cisternacli1> list() throws BusinessException {
        try {
            return cisternacli1Repository.findAll();
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }

    @Override
    @Transactional
    public Cisternacli1 add(Cisternacli1 cisterna) throws FoundException, BusinessException {
        try {
            Optional<Cisternacli1> existing = cisternacli1Repository.findOneByIdCli1(cisterna.getIdCli1());
            if (existing.isPresent())
                throw new FoundException("Ya existe cisterna con idCli1: " + cisterna.getIdCli1());
            return cisternacli1Repository.save(cisterna);
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }

    @Override
    @Transactional
    public Cisterna loadOrCreate(Cisternacli1 cisterna) throws BusinessException, NotFoundException {
        try {
            Optional<Cisternacli1> existing = cisternacli1Repository.findOneByIdCli1(cisterna.getIdCli1());
            if (existing.isPresent()) {
                return existing.get();
            } else {
                return cisternacli1Repository.save(cisterna);
            }
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }
}
