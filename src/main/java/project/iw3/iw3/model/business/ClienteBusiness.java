package project.iw3.iw3.model.business;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.micrometer.common.lang.Nullable;
import jakarta.transaction.Transactional;
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
    private ClienteRepository clienteDAO;

    @Override
    public List<Cliente> list() throws BusinessException {
        try {
            return clienteDAO.findAll();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public Cliente load(long id) throws NotFoundException, BusinessException {
        Optional<Cliente> r;
        try {
            r = clienteDAO.findById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (r.isEmpty()) {
            throw NotFoundException.builder()
                    .message("No se encuentra el Cliente id = " + id)
                    .build();
        }
        return r.get();
    }

    @Override
    public Cliente load(String nombreEmpresa) throws NotFoundException, BusinessException {
        Optional<Cliente> r;
        try {
            r = clienteDAO.findByNombreEmpresa(nombreEmpresa);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (r.isEmpty()) {
            throw NotFoundException.builder()
                    .message("No se encuentra el Cliente con razón social '" + nombreEmpresa + "'")
                    .build();
        }
        return r.get();
    }

    @Override
    public Cliente add(Cliente cliente) throws FoundException, BusinessException {
        // Verifico existencia por id
        try {
            if (cliente.getId() != null) {
                load(cliente.getId());
                throw FoundException.builder()
                        .message("Se encontró el Cliente id=" + cliente.getId())
                        .build();
            }
        } catch (NotFoundException e) {
            // Si no existe por id, seguimos
        }

        // Verifico unicidad por nombreEmpresa
        try {
            load(cliente.getNombreEmpresa());
            throw FoundException.builder()
                    .message("Se encontró el Cliente con razón social '" + cliente.getNombreEmpresa() + "'")
                    .build();
        } catch (NotFoundException e) {
            // No existe por nombre, seguimos a guardar
        }

        try {
            return clienteDAO.save(cliente);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public Cliente update(Cliente cliente) throws FoundException, NotFoundException, BusinessException {
        // Debe existir el id
        load(cliente.getId());

        // Unicidad de nombreEmpresa contra otros registros
        Optional<Cliente> nombreExistente;
        try {
            nombreExistente = clienteDAO.findByNombreEmpresaAndIdNot(
                    cliente.getNombreEmpresa(), cliente.getId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }

        if (nombreExistente != null && nombreExistente.isPresent()) {
            throw FoundException.builder()
                    .message("Se encontró otro Cliente con razón social = " + cliente.getNombreEmpresa())
                    .build();
        }

        try {
            return clienteDAO.save(cliente);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public void delete(long id) throws NotFoundException, BusinessException {
        // Verifico que exista
        load(id);

        try {
            clienteDAO.deleteById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    // Patrón loadOrCreate al estilo del profe (igual que Chofer)
    @Override
    @Transactional
    public Cliente loadOrCreate(String nombreEmpresa, @Nullable String email) throws BusinessException {
        if (nombreEmpresa == null || nombreEmpresa.isBlank()) {
            throw new BusinessException("Cliente: 'nombreEmpresa' es obligatorio.");
        }
        final String nom = nombreEmpresa.trim();

        try {
            Optional<Cliente> found = clienteDAO.findByNombreEmpresa(nom);
            if (found.isPresent()) {
                log.debug("Cliente existente recuperado: {}", found.get().getNombreEmpresa());
                return found.get();
            }

            Cliente nuevo = new Cliente();
            nuevo.setNombreEmpresa(nom);
            nuevo.setEmail(email);
            Cliente saved = clienteDAO.save(nuevo);
            log.info("Cliente creado: {}", saved.getNombreEmpresa());
            return saved;

        } catch (Exception e) {
            log.error("Error creando/recuperando cliente {}: {}", nom, e.getMessage(), e);
            throw new BusinessException("Error creando cliente: " + e.getMessage(), e);
        }
    }
}
            