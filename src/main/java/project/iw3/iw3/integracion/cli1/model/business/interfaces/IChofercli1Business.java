package project.iw3.iw3.integracion.cli1.model.business.interfaces;

import project.iw3.iw3.integracion.cli1.model.Chofercli1;
import project.iw3.iw3.model.Chofer;
import project.iw3.iw3.model.business.exceptions.*;

import java.util.List;

public interface IChofercli1Business {

    Chofercli1 load(String idCli1) throws NotFoundException, BusinessException;

    List<Chofercli1> list() throws BusinessException;

    Chofercli1 add(Chofercli1 chofer) throws FoundException, BusinessException;

    Chofer loadOrCreate(Chofercli1 chofer) throws BusinessException, NotFoundException;
}
