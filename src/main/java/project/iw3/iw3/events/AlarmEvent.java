package project.iw3.iw3.events;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;
import lombok.Setter;
import project.iw3.iw3.model.Orden;

@Getter
@Setter
public class AlarmEvent extends ApplicationEvent {
	
	 public enum TypeEvent {
	        TEMPERATURA_SUPERADA
	    }

	    public AlarmEvent(Object source, Object orden, TypeEvent typeEvent) {
	        super(source);
		 	this.orden = orden;
	        this.typeEvent = typeEvent;
	    }

	    private TypeEvent typeEvent;
	    private Object orden;
	
		
}
