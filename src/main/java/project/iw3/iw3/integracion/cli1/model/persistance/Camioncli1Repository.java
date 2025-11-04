package project.iw3.iw3.integracion.cli1.model.persistance;

import project.iw3.iw3.integracion.cli1.model.Camioncli1;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface Camioncli1Repository extends JpaRepository<Camioncli1, Long> {

    Optional<Camioncli1> findOneByIdCli1(String idCli1);

    @Modifying
    @Query(value = "INSERT INTO cli1_camiones (id_camion, id_cli1) VALUES (:idCamion, :idCli1)", nativeQuery = true)
    void insertCamionCli1(@Param("idCamion") Long idCamion, @Param("idCli1") String idCli1);
}
