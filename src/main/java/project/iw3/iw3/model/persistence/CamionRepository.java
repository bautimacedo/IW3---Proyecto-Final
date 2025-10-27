package project.iw3.iw3.model.persistence;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.iw3.iw3.model.Camion;

@Repository
public interface CamionRepository extends JpaRepository<Camion, Long> 
{
    Optional<Camion> findByPatente(String Patente);

    Optional<Camion> findByPatenteAndIdNot(String Patente, long id);
}
