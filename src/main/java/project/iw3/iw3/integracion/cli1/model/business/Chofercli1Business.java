package project.iw3.iw3.integracion.cli1.model.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.iw3.iw3.integracion.cli1.model.Chofercli1;
import project.iw3.iw3.integracion.cli1.model.persistance.Chofercli1Repository;
import project.iw3.iw3.integracion.cli1.model.business.interfaces.IChofercli1Business;
import project.iw3.iw3.model.Chofer;
import project.iw3.iw3.model.business.exceptions.*;

import java.util.List;
import java.util.Optional;

@Service
public class Chofercli1Business implements IChofercli1Business {

    @Autowired
    private Chofercli1Repository chofercli1Repository;

    @Override
    public Chofercli1 load(String idCli1) throws NotFoundException, BusinessException {
        try {
            return chofercli1Repository.findOneByIdCli1(idCli1)
                    .orElseThrow(() -> new NotFoundException("Chofer CLI1 no encontrado: " + idCli1));
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }

    @Override
    public List<Chofercli1> list() throws BusinessException {
        try {
            return chofercli1Repository.findAll();
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }

    @Override
    @Transactional
    public Chofercli1 add(Chofercli1 chofer) throws FoundException, BusinessException {
        try {
            Optional<Chofercli1> existing = chofercli1Repository.findOneByIdCli1(chofer.getIdCli1());
            if (existing.isPresent())
                throw new FoundException("Ya existe chofer con idCli1: " + chofer.getIdCli1());
            return chofercli1Repository.save(chofer);
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }

    @Override
    @Transactional
    public Chofer loadOrCreate(Chofercli1 chofer) throws BusinessException, NotFoundException {
        try {
            Optional<Chofercli1> existing = chofercli1Repository.findOneByIdCli1(chofer.getIdCli1());
            if (existing.isPresent()) {
                return existing.get();
            } else {
                return chofercli1Repository.save(chofer);
            }
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }
}
