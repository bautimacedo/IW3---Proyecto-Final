package project.iw3.iw3.model.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import project.iw3.iw3.model.Producto;
import project.iw3.iw3.model.persistence.ProductoRepository;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class ProductoBusiness implements IProductoBusiness {

    @Autowired
	private ProductoRepository productDAO;

    @Override
    public Producto add(Producto producto) throws BusinessException {
        return productDAO.save(producto);
    
}

}
