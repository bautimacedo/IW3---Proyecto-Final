package project.iw3.iw3.integracion.cli1.model;
import project.iw3.iw3.model.Producto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "cli1_productos")
@PrimaryKeyJoinColumn(name = "id_chofer")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Productocli1 extends Producto {

    @Column(nullable = false, unique = true)
    private String idCli1; //Este es el Id del producto en el sistema externo

}