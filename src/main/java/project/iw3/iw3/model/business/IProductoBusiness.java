package project.iw3.iw3.model.business;

import project.iw3.iw3.model.Producto;

public interface IProductoBusiness {
    
    public Producto add(Producto producto) throws BusinessException;

    //public Producto update(Producto producto) throws BusinessException, NotFoundException;

}
