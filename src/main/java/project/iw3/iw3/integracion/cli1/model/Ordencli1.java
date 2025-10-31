package project.iw3.iw3.integracion.cli1.model;
import project.iw3.iw3.model.Orden;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "cli1_ordenes")
@PrimaryKeyJoinColumn(name = "id_orden")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Ordencli1 extends Orden {

    @Column(nullable = false, unique = true)
    private String numeroOrdenCli1; //Este es el numero de orden en el sistema externo


}