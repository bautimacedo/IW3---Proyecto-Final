package project.iw3.iw3.model.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.iw3.iw3.model.Cisterna;
import project.iw3.iw3.model.business.exceptions.BusinessException;
import project.iw3.iw3.model.business.exceptions.FoundException;
import project.iw3.iw3.model.business.exceptions.NotFoundException;
import project.iw3.iw3.model.business.interfaces.ICisternaBusiness;
import project.iw3.iw3.model.persistence.CisternaRepository;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Service
public class CisternaBusiness implements ICisternaBusiness {

    @Autowired
    private CisternaRepository cisternaRepository;

    @Override
    public List<Cisterna> list() throws BusinessException {
        try {
            return cisternaRepository.findAll();
        } catch (Exception e) {
            throw new BusinessException(e.getMessage(), e);
        }
    }

    @Override
    public Cisterna load(long id) throws NotFoundException, BusinessException {
        try {
            return cisternaRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("No existe la cisterna con id " + id));
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(e.getMessage(), e);
        }
    }

    @Override
    public Cisterna load(String licencia) throws NotFoundException, BusinessException {
        try {
            return cisternaRepository.findByLicencia(licencia)
                    .orElseThrow(() -> new NotFoundException("No existe la cisterna con licencia " + licencia));
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(e.getMessage(), e);
        }
    }

    @Override
    public Cisterna add(Cisterna cisterna) throws FoundException, BusinessException {
        try {
            if (cisternaRepository.findByLicencia(cisterna.getLicencia()).isPresent()) {
                throw new FoundException("Ya existe una cisterna con esa licencia");
            }
            return cisternaRepository.save(cisterna);
        } catch (FoundException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(e.getMessage(), e);
        }
    }

    @Override
    public Cisterna update(Cisterna cisterna) throws FoundException, NotFoundException, BusinessException {
        try {
            // validar existencia
            load(cisterna.getId());

            // validar duplicado de licencia
            if (cisternaRepository.findByLicenciaAndIdNot(cisterna.getLicencia(), cisterna.getId()).isPresent()) {
                throw new FoundException("Ya existe otra cisterna con esa licencia");
            }
            return cisternaRepository.save(cisterna);
        } catch (FoundException | NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(e.getMessage(), e);
        }
    }

     @Override
    public void delete(long id) throws NotFoundException, BusinessException {
        load(id);

        try {
            cisternaRepository.deleteById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }
   

}
