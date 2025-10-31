package project.iw3.iw3.integracion.cli1.model.persistance;

import project.iw3.iw3.integracion.cli1.model.Chofercli1;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface Chofercli1Repository extends JpaRepository<Chofercli1, Long> {

    Optional<Chofercli1> findOneByIdCli1(String idCli1);

    @Modifying
    @Query(value = "INSERT INTO cli1_choferes (id_chofer, id_cli1) VALUES (:idChofer, :idCli1)", nativeQuery = true)
    void insertChoferCli1(@Param("idChofer") Long idChofer, @Param("idCli1") String idCli1);
}
