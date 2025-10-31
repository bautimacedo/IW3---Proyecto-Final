package project.iw3.iw3.integracion.cli1.model;
import project.iw3.iw3.model.Cliente;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "cli1_clientes")
@PrimaryKeyJoinColumn(name = "id_cliente")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Clientecli1 extends Cliente {

    @Column(nullable = false, unique = true)
    private String idCli1; //Este es el Id del cliente en el sistema externo

}