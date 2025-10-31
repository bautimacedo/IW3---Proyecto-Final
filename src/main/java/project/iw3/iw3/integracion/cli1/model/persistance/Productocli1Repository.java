package project.iw3.iw3.integracion.cli1.model.persistance;

import project.iw3.iw3.integracion.cli1.model.Productocli1;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface Productocli1Repository extends JpaRepository<Productocli1, Long> {

    Optional<Productocli1> findOneByIdCli1(String idCli1);

    @Modifying
    @Query(value = "INSERT INTO cli1_productos (id_producto, id_cli1) VALUES (:idProducto, :idCli1)", nativeQuery = true)
    void insertProductoCli1(@Param("idProducto") Long idProducto, @Param("idCli1") String idCli1);
}
