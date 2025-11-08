package project.iw3.iw3.model.business.interfaces;

import java.util.List;

import project.iw3.iw3.model.ConciliacionDTO;
import project.iw3.iw3.model.DatosCargaDTO;
import project.iw3.iw3.model.Orden;
import project.iw3.iw3.model.business.exceptions.*;

public interface IOrdenBusiness {
	
	public List<Orden> list() throws BusinessException;
	
	public Orden load(Integer numeroOrden) throws NotFoundException, BusinessException; //carga de orden por numero de orden
	
	
	public Orden update(Orden orden) throws FoundException,NotFoundException,BusinessException; //Actualizar
	
	public void delete(Integer numeroOrden) throws NotFoundException, BusinessException; //eliminar por numero de orden
	
	public Orden add(Orden orden) throws FoundException, BusinessException; //agregar una orden
	
	//punto1
	public Orden addExternal(String json) throws FoundException, BusinessException; 
    
	//punto2
	public Orden registrarPesoInicial (String patente, float tara) throws BusinessException, NotFoundException, FoundException;

	//punto4
	public Orden cerrarOrden(Integer numeroOrden) throws BusinessException, NotFoundException, FoundException; 

  	public Orden recibirDatosCarga(DatosCargaDTO datos)throws BusinessException, NotFoundException;

	public Orden loadByNumeroOrden(Integer numeroOrden) throws NotFoundException, BusinessException;

    public ConciliacionDTO getConciliacion(Integer numeroOrden) throws NotFoundException, BusinessException;

	public ConciliacionDTO registrarPesajeFinal(Integer numeroOrden, Double pesoFinal) throws NotFoundException, BusinessException;
}
