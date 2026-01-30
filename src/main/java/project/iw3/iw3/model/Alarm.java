package project.iw3.iw3.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project.iw3.iw3.auth.User;

import java.util.Date;

@Entity
@Table(name = "alarm")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Alarm {
	
	public enum Estado {
        PENDIENTE_REVISION,  // La alarma ha sido generada pero aún no ha sido revisada por ningún operador.
        ACEPTADA    // La alarma ha sido revisada y aceptada, indicando que está bajo control.
    }
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "id_order", nullable = false)
    private Orden orden;

    @Enumerated(EnumType.STRING)
    @Column()
    private Alarm.Estado estado;

    @Column(nullable = false)
    private Date timeStamp;

    @Column(nullable = false)
    private double temperatura;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;

    private String descripcion;
    
    
}
