package project.iw3.iw3.model;

import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Entity
@Table(name = "clientes")
@Inheritance(strategy = InheritanceType.JOINED)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 100, unique = true, nullable = false)
    private String nombreEmpresa; //Seria el nombre q figura en la AFIP
    //private String nombrelegal; Es otra opcion

    @Column(length = 50)
    private String email; //Este es el contacto
}
