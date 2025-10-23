package project.iw3.iw3.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "camiones")
@Inheritance(strategy = InheritanceType.JOINED)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Camion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 100, unique = true, nullable = false)
    private String patente;

    private String descripcion;

    //Un camion puede tener varias cisternas
    @OneToMany(mappedBy = "camion")
    private Set<Cisterna> cisterna = new HashSet<>();
}
