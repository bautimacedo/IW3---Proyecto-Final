package project.iw3.iw3.model.business;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import io.micrometer.common.lang.Nullable;
import jakarta.transaction.Transactional;
import project.iw3.iw3.model.Producto;
import project.iw3.iw3.model.business.exceptions.BusinessException;
import project.iw3.iw3.model.business.exceptions.FoundException;
import project.iw3.iw3.model.business.exceptions.NotFoundException;
import project.iw3.iw3.model.business.interfaces.IProductoBusiness;
import project.iw3.iw3.model.persistence.ProductoRepository;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class ProductoBusiness implements IProductoBusiness {

    @Autowired
	private ProductoRepository productDAO;

    @Override
    public Producto add(Producto producto) throws FoundException, BusinessException {
        try {
            load(producto.getId());
            throw FoundException.builder().message("Se encontró el Producto id=" + producto.getId()).build();
        } catch (NotFoundException e) {
            // si no existe, seguimos
        }
    
        try {
            load(producto.getNombre());
            throw FoundException.builder().message("Se encontro el producto '" + producto.getNombre() + "'").build();
        }catch(NotFoundException e) {
            // si no existe, continuamos
        }

        try {
            return productDAO.save(producto);
        }catch(Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public Producto load(long id) throws NotFoundException, BusinessException {
        Optional<Producto> r;
        try {
            r = productDAO.findById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if(r.isEmpty()) {
            throw NotFoundException.builder().message("No se encuentra el Producto id =" + id).build();
        }
        return r.get();
    }

    @Override
    public List<Producto> list() throws BusinessException {
        try{
            return productDAO.findAll();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public Producto update(Producto producto)
            throws FoundException, NotFoundException, BusinessException {

        // valida que exista el ID
        load(producto.getId());

        // valida que no haya OTRO con el mismo nombre
        Optional<Producto> nombreExistente = null;
        try {
            nombreExistente = productDAO.findByNombreAndIdNot(
                    producto.getNombre(),
                    producto.getId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }

        if (nombreExistente != null && nombreExistente.isPresent()) {
            throw FoundException.builder()
                    .message("Se encontró otro producto con nombre=" + producto.getNombre())
                    .build();
        }

        try {
            return productDAO.save(producto);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public void delete(long id) throws NotFoundException, BusinessException {
        load(id);

        try {
            productDAO.deleteById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public Producto load(String nombre) throws NotFoundException, BusinessException {
         Optional<Producto> r;
        try {
            r = productDAO.findByNombre(nombre);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if(r.isEmpty()) {
            throw NotFoundException.builder().message("No se encuentra el Producto '" + nombre + "'").build();
        }
        return r.get();
    }

	@Override
@Transactional
public Producto loadOrCreate(String nombre, @Nullable String descripcion, Float temperatura_umbral) throws BusinessException {
    // 1) Validacion basica
    if (nombre == null || nombre.isBlank()) {
        throw new BusinessException("Producto: 'nombre' es obligatorio.");
    }

    final String nom = nombre.trim().toUpperCase();

    // 2) Buscar primero
    Optional<Producto> found = productDAO.findByNombre(nom);
    if (found.isPresent()) {
        log.debug("Producto existente recuperado: {}", found.get().getNombre());
        return found.get();
    }

    // 3) Crear si no existe
    try {
        Producto nuevo = new Producto();
        nuevo.setNombre(nom);
        nuevo.setDescripcion(descripcion); // puede ser null
        nuevo.setTemperatura_umbral(temperatura_umbral);
        Producto saved = productDAO.save(nuevo);
        log.info("Producto creado: nombre={}", saved.getNombre());
        return saved;
    } catch (DataIntegrityViolationException e) {
        // 4) Si hubo colision de insercion, reintentamos leer
        log.warn("Colision creando producto {}, releyendo: {}", nom, e.getMessage());
        return productDAO.findByNombre(nom)
                .orElseThrow(() -> new BusinessException("No se pudo crear ni recuperar el producto con nombre=" + nom));
    } catch (Exception e) {
        log.error("Error creando producto {}: {}", nom, e.getMessage(), e);
        throw new BusinessException("Error creando producto: " + e.getMessage(), e);
    }
}


}
