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
    private Integer numeroOrden; //?> revisar

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
    private Float preset; //esto no se esta guardando en el punto 1)

    @Column(nullable = true)
    private Float tara; // punto 2)

    @Column(nullable = true)
    private Double pesoFinal; // punto 5) cuando se pesa finalmente el camion con la carga.
    
    
    
    
    
    @Column(nullable = true)
    private Date fechaRecepcionInicial; // cuando se creó la orden hay que agregarlo con el punto 1)
    
    @Column(nullable = true)
    private Date fechaPesajeTara; // punto 2)
    
    @Column(nullable = true)
    private Date fechaInicioCarga; // primer dato de carga primer masa primer caudal etc.
    
    @Column(nullable = true)
    private Date ultimaFechaInformacion; //punto 3 es la ultima fecha que se cargo un dato
    
    @Column(nullable = true)
    private Date fechaCierreCarga; // punto 4) la fecha en la que no se puede cargar mas carga, cambio de estado.
    
    @Column(nullable = true)
    private Date fechaCierreDeOrden; // punto 5)
    
    
    @Column(nullable = true)
    private Double promedioDensidad;

    @Column(nullable = true)
    private Double promedioTemperatura;

    @Column(nullable = true)
    private Double promedioCaudal;

    
    

    @Column(nullable = true)
    private Double ultimaMasaAcumulada;

    @Column(nullable = true)
    private Double ultimaDensidad;

    @Column(nullable = true)
    private Double ultimaTemperatura;

    @Column(nullable = true)
    private Double ultimaFlowRate; // esto es el caudal.

    
    //para las alarma. Cuando viene una temperatura superada entonces salta la alarma. Si ya salto, no tiene que saltar de nuevo hasta que sea aceptada.
    @Column(nullable = false)
    private boolean alarmaActivada = false;







}
