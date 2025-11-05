package project.iw3.iw3.model;

import project.iw3.iw3.model.enums.EstadoOrden;
import java.util.Date;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "orden")
@Inheritance(strategy = InheritanceType.JOINED)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Orden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoOrden estadoOrden;

    @Column(unique = true)
    private Integer password;

    @Column(unique = true)
    private Integer numeroOrden;

    @ManyToOne
    @JoinColumn(name = "id_producto")
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "id_camion")
    private Camion camion;

    @ManyToOne
    @JoinColumn(name = "id_chofer")
    private Chofer chofer;

    @ManyToOne
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    // Campos numéricos flexibles (pueden ser nulos)
    @Column(nullable = true)
    private Float preset;

    @Column(nullable = true)
    private Float tara;

    @Column(nullable = true)
    private Double pesoFinal;

    @Column(nullable = true)
    private Date ultimaFechaInformacion;

    @Column(nullable = true)
    private Double ultimaMasaAcumulada;

    @Column(nullable = true)
    private Double ultimaDensidad;

    @Column(nullable = true)
    private Double ultimaTemperatura;

    @Column(nullable = true)
    private Double ultimaFlowRate;

    @Column(nullable = true)
    private Date fechaPrevistaDeCarga;
}
