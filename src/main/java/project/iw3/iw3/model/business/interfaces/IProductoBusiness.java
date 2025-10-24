package project.iw3.iw3.model.business.interfaces;

import project.iw3.iw3.model.Producto;
import project.iw3.iw3.model.business.exceptions.BusinessException;

public interface IProductoBusiness {
    
    public Producto add(Producto producto) throws BusinessException;

    //public Producto update(Producto producto) throws BusinessException, NotFoundException;

}
