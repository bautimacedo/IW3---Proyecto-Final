package project.iw3.iw3.model.business;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import project.iw3.iw3.model.Alarm;
import project.iw3.iw3.model.business.exceptions.BusinessException;
import project.iw3.iw3.model.business.exceptions.FoundException;
import project.iw3.iw3.model.business.exceptions.NotFoundException;
import project.iw3.iw3.model.business.interfaces.IAlarmBusiness;
import project.iw3.iw3.model.persistence.AlarmRepository;
import project.iw3.iw3.model.business.interfaces.*;

@Service
@Slf4j
public class AlarmBusiness implements IAlarmBusiness {
	
	@Autowired
    private AlarmRepository alarmDAO;

	@Override
	public List<Alarm> list() throws BusinessException {
		try {
            return alarmDAO.findAll();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
	}

	@Override
	public Alarm load(long id) throws NotFoundException, BusinessException {
		Optional<Alarm> alarmFound;
        try {
            alarmFound = alarmDAO.findById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (alarmFound.isEmpty())
        	throw NotFoundException.builder()
            .message("NO se encuentra la alarma= " + id)
            .build();
        return alarmFound.get();
	}

	@Override
	public Alarm add(Alarm alarm) throws FoundException, BusinessException {
		try {
			load(alarm.getId());
			
			throw FoundException.builder()
            .message("Ya existe la alarma con el id = " + alarm.getId())
            .build();
			
		}catch(NotFoundException e) {
			//no existe, seguimos.
		}
		
		try {
            return alarmDAO.save(alarm);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
	}

	@Override
	public Alarm update(Alarm alarm) throws NotFoundException, BusinessException {
		load(alarm.getId());
        try {
            return alarmDAO.save(alarm);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
	}

}
