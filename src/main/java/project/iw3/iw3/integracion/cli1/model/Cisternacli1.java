package project.iw3.iw3.integracion.cli1.model;
import project.iw3.iw3.model.Cisterna;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "cli1_cisternas")
@PrimaryKeyJoinColumn(name = "id_cisterna")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Cisternacli1 extends Cisterna {

    @Column(nullable = false, unique = true)
    private String idCli1; //Este es el Id de la cisterna en el sistema externo

}