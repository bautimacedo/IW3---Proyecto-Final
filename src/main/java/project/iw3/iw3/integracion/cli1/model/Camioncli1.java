package project.iw3.iw3.integracion.cli1.model;
import project.iw3.iw3.model.Camion;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "cli1_camiones")
@PrimaryKeyJoinColumn(name = "id_camion")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Camioncli1 extends Camion {

    @Column(nullable = false, unique = true)
    private String idCli1; //Este es el Id del camion en el sistema externo

}