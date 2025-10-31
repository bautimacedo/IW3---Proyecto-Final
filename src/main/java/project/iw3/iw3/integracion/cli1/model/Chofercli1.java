package project.iw3.iw3.integracion.cli1.model;
import project.iw3.iw3.model.Chofer;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "cli1_choferes")
@PrimaryKeyJoinColumn(name = "id_chofer")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Chofercli1 extends Chofer {

    @Column(nullable = false, unique = true)
    private String idCli1; //Este es el Id del chofer en el sistema externo

}