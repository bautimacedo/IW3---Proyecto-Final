package project.iw3.iw3.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cisternas")
@Inheritance(strategy = InheritanceType.JOINED)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Cisterna {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonBackReference
    @JoinColumn(name = "id_camion", nullable = false) //Foreign key hacia camion
    @ManyToOne(fetch = FetchType.LAZY) //Varias cisternas pueden estar en un camion
    private Camion camion;

    @Column(nullable = false)
    private long capacidadLitros;

    @Column(nullable = false)
    private String licencia;
}
