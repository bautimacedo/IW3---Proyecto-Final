package project.iw3.iw3.integracion.cli1.model.persistance;

import project.iw3.iw3.integracion.cli1.model.Ordencli1;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface Ordencli1Repository extends JpaRepository<Ordencli1, Long> {

    Optional<Ordencli1> findOneByIdCli1(String idCli1);

    @Modifying
    @Query(value = "INSERT INTO cli1_ordenes (id_orden, id_cli1) VALUES (:idOrden, :idCli1)", nativeQuery = true)
    void insertOrdenCli1(@Param("idOrden") Long idOrden, @Param("idCli1") String idCli1);
}
