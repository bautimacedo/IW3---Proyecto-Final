package project.iw3.iw3.events.listeners;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import project.iw3.iw3.util.EmailBusiness;


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

	@Autowired
	private EmailBusiness emailBusiness;

	@Value("${alarm.mail.to:}")
	private String alarmMailTo;

	private void handlerTemperaturaSuperada(DatosCargaDTO datos, Orden orden) {
		
		    try {
				if (alarmBusiness.existsPendingAlarm(orden)) {
					log.info("Ya existe alarma pendiente para orden {}", orden.getNumeroOrden());
					return;
				}
			} catch (BusinessException e) {
				log.error("Error verificando alarmas pendientes", e);
				return;
			}

		
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
	    
	    // Envío de mail si está configurado el destinatario
	    if (alarmMailTo != null && !alarmMailTo.isBlank()) {
	        try {
	            String subject = String.format("Alarma: Temperatura superada - Orden %d", orden.getNumeroOrden());
	            String text = String.format(
	                    "Se registró temperatura superada.%nOrden: %d%nTemperatura: %.2f °C%nFecha/Hora: %s%nEstado: %s%nDescripción: %s",
	                    orden.getNumeroOrden(),
	                    datos.getTemperatura(),
	                    alarm.getTimeStamp(),
	                    alarm.getEstado(),
	                    alarm.getDescripcion()
	            );
	            emailBusiness.sendSimpleMessage(alarmMailTo, subject, text);
	            log.info("Mail de alarma enviado a {} por orden {}", alarmMailTo, orden.getNumeroOrden());
	        } catch (BusinessException e) {
	            log.error("Error al enviar mail de alarma: {}", e.getMessage(), e);
	        }
	    } else {
	        log.warn("No se configuró alarm.mail.to, no se envía mail de alarma");
	    }
	}
	
	
}
