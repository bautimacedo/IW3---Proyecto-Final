package project.iw3.iw3.events.listeners;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import project.iw3.iw3.events.AlarmEvent;
import project.iw3.iw3.model.Alarm;
import project.iw3.iw3.model.DatosCargaDTO;
import project.iw3.iw3.model.Orden;
import project.iw3.iw3.model.business.AlarmBusiness;
import project.iw3.iw3.model.business.exceptions.BusinessException;
import project.iw3.iw3.model.business.exceptions.FoundException;


@Slf4j
@Component
public class AlarmEventListener implements ApplicationListener<AlarmEvent> {

	
	@Override
    public void onApplicationEvent(AlarmEvent event) {
        if (event.getTypeEvent().equals(AlarmEvent.TypeEvent.TEMPERATURA_SUPERADA)) {
            handlerTemperaturaSuperada((DatosCargaDTO) event.getSource(), (Orden) event.getOrden()); //obtengo del source los datos de carga
        }
    }
	
	
	 @Autowired
	 private AlarmBusiness alarmBusiness;
		
	private void handlerTemperaturaSuperada(DatosCargaDTO datos, Orden orden) {
		
		//primero creamos la alarma
		
		Alarm alarm = new Alarm();
		alarm.setTimeStamp(new Date(System.currentTimeMillis()));
	    alarm.setTemperatura (datos.getTemperatura());
	    alarm.setEstado(Alarm.Estado.PENDIENTE_REVISION);
	    alarm.setOrden(orden);
	    alarm.setDescripcion("Alarma creada pendiente a revisión por operario");
		
	    //guardo la alarma en la BD
	    try {
            alarmBusiness.add(alarm);
        } catch (BusinessException | FoundException e) {
            log.error(e.getMessage(), e);
        }
	    
	    //envio de mail
	    //TODO
		
		
	}
	
	
}
