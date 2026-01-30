package project.iw3.iw3.model.business.interfaces;

import java.util.List;


import project.iw3.iw3.model.Alarm;
import project.iw3.iw3.model.business.exceptions.BusinessException;
import project.iw3.iw3.model.business.exceptions.FoundException;
import project.iw3.iw3.model.business.exceptions.NotFoundException;

public interface IAlarmBusiness {
	
	public List<Alarm> list() throws BusinessException;

    public Alarm load(long id) throws NotFoundException, BusinessException;

    public Alarm add(Alarm alarm) throws FoundException, BusinessException;

    Alarm update(Alarm alarm) throws NotFoundException, BusinessException;

}
