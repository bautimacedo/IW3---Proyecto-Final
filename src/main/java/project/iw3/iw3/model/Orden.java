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
    
	//Primary Key de orden --> id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    // Estado actual de la orden
    @Enumerated(EnumType.STRING)
    @Column()
    private EstadoOrden estadoOrden;
   
    // Contrasenia generada para la carga del producto. unique es que no puede haber 2 filas con el mismo valor.
    @Column(unique = true)
    private Integer password;
    
    
    @Column(unique = true)
    private Integer numeroOrden; //este lo podemos usar para el numero de orden que viene desde el camion directamente.
    
    
    
    
    
    
    
    // va a tener un tipo de producto a cargar --> Muchas ordenes asociadas a un solo producto.
    //id_producto es el nombre de la columna FK que se creara en la tabla orden
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
    
    
    
    
    
    
    
    // Cantidad de producto a cargar
    @Column()
    private Float preset;
    
    // peso sin producto
    @Column()
    private Float tara;
    
    // peso con producto
    @Column()
    private Float pesoFinal;
    
    
    
    
    @Column()
    private Date ultimaFechaInformacion;
    
    @Column()
    private Float ultimaMasaAcumulada;
    
    @Column()
    private Float ultimaDensidad;
    
    @Column()
    private Float ultimaTemperatura;
    
    @Column()
    private Float ultimaFlowRate;
    
    

    @Column()
    private Date fechaPrevistaDeCarga; // hay que agregar esto.
}
