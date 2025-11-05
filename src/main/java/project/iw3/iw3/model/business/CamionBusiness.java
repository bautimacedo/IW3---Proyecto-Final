package project.iw3.iw3.model.business;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import io.micrometer.common.lang.Nullable;
import jakarta.transaction.Transactional;
import project.iw3.iw3.model.Camion;
import project.iw3.iw3.model.Chofer;
import project.iw3.iw3.model.Cisterna;
import project.iw3.iw3.model.business.exceptions.BusinessException;
import project.iw3.iw3.model.business.exceptions.FoundException;
import project.iw3.iw3.model.business.exceptions.NotFoundException;
import project.iw3.iw3.model.business.interfaces.ICamionBusiness;
import project.iw3.iw3.model.persistence.CamionRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CamionBusiness implements ICamionBusiness {

    @Autowired
    private CamionRepository camionRepository;

    @Override
    public List<Camion> list() throws BusinessException {
        try {
            return camionRepository.findAll();
        } catch (Exception e) {
            throw new BusinessException(e.getMessage(), e);
        }
    }

    @Override
    public Camion load(long id) throws NotFoundException, BusinessException {
        try {
            return camionRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("No existe el camión con id " + id));
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(e.getMessage(), e);
        }
    }

    @Override
    public Camion load(String patente) throws NotFoundException, BusinessException {
        try {
            return camionRepository.findByPatente(patente)
                    .orElseThrow(() -> new NotFoundException("No existe el camión con la patente " + patente));
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(e.getMessage(), e);
        }
    }

    @Override
    public Camion add(Camion camion) throws FoundException, BusinessException {
        try {
            // Validar duplicado por patente
            if (camionRepository.findByPatente(camion.getPatente()).isPresent()) {
                throw new FoundException("Ya existe un camión con esa patente");
            }
            return camionRepository.save(camion);
        } catch (FoundException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(e.getMessage(), e);
        }
    }

    @Override
    public Camion update(Camion camion) throws FoundException, NotFoundException, BusinessException {
        try {
            // Validar existencia
            load(camion.getId());

            // Validar duplicado en OTRO camión
            if (camionRepository.findByPatenteAndIdNot(camion.getPatente(), camion.getId()).isPresent()) {
                throw new FoundException("Ya existe otro camión con esa patente");
            }

            return camionRepository.save(camion);
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
            camionRepository.deleteById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

	 @Override
@Transactional
public Camion loadOrCreate(String patente, @Nullable String descripcion) throws BusinessException {
    //1. Validar datos básicos
    if (patente == null || patente.isBlank()) {
        throw new BusinessException("Camión: 'patente' es obligatoria.");
    }

    final String pat = patente.trim().toUpperCase();

    // 2. ntentar buscar el camion existente
    Optional<Camion> found = camionRepository.findByPatente(pat);
    if (found.isPresent()) {
        log.debug("Camion existente recuperado: {}", found.get().getPatente());
        return found.get();
    }

    // 3. Si no existe, creamos uno nuevo (sin procesar cisternas)
    try {
        Camion nuevo = new Camion();
        nuevo.setPatente(pat);
        nuevo.setDescripcion(descripcion); // puede ser null
        Camion saved = camionRepository.save(nuevo);
        log.info("Camion creado: patente={}", saved.getPatente());
        return saved;
    } catch (DataIntegrityViolationException e) {
        // 4️⃣ Si otro hilo lo creó mientras tanto, reintentar leer
        log.warn("Colision creando camión {}, releyendo: {}", pat, e.getMessage());
        return camionRepository.findByPatente(pat)
                .orElseThrow(() -> new BusinessException("No se pudo crear ni recuperar el camion con patente=" + pat));
    } catch (Exception e) {
        log.error("Error creando camión {}: {}", pat, e.getMessage(), e);
        throw new BusinessException("Error creando camion: " + e.getMessage(), e);
    }
}


   
}    