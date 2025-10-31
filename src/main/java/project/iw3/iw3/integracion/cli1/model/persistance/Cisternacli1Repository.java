package project.iw3.iw3.integracion.cli1.model.persistance;

import project.iw3.iw3.integracion.cli1.model.Cisternacli1;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface Cisternacli1Repository extends JpaRepository<Cisternacli1, Long> {

    Optional<Cisternacli1> findOneByIdCli1(String idCli1);

    @Modifying
    @Query(value = "INSERT INTO cli1_cisternas (id_cisterna, id_cli1) VALUES (:idCisterna, :idCli1)", nativeQuery = true)
    void insertCisternaCli1(@Param("idCisterna") Long idCisterna, @Param("idCli1") String idCli1);
}
