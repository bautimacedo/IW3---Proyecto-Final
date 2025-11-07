package project.iw3.iw3.model.business.interfaces;

import java.util.List;

import io.micrometer.common.lang.Nullable;
import project.iw3.iw3.model.Producto;
import project.iw3.iw3.model.business.exceptions.BusinessException;
import project.iw3.iw3.model.business.exceptions.FoundException;
import project.iw3.iw3.model.business.exceptions.NotFoundException;

public interface IProductoBusiness {
    
    public Producto add(Producto producto) throws FoundException, BusinessException;

    public Producto load(long id) throws NotFoundException, BusinessException;

    public Producto load(String nombre) throws NotFoundException, BusinessException;

    public List<Producto> list() throws BusinessException;
    
    public Producto update(Producto producto) throws FoundException, BusinessException, NotFoundException;
    
    public void delete(long id) throws NotFoundException, BusinessException;
    
    public Producto loadOrCreate(String nombre, @Nullable String descripcion) throws BusinessException;

}
