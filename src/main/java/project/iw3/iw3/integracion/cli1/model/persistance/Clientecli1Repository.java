package project.iw3.iw3.integracion.cli1.model.persistance;

import project.iw3.iw3.integracion.cli1.model.Clientecli1;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface Clientecli1Repository extends JpaRepository<Clientecli1, Long> {

    Optional<Clientecli1> findOneByIdCli1(String idCli1);

    @Modifying
    @Query(value = "INSERT INTO cli1_clientes (id_cliente, id_cli1) VALUES (:idCliente, :idCli1)", nativeQuery = true)
    void insertClienteCli1(@Param("idCliente") Long idCliente, @Param("idCli1") String idCli1);
}
