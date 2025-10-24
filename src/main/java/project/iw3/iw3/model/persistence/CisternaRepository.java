package project.iw3.iw3.model.persistence;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.iw3.iw3.model.Cisterna;

@Repository
public interface CisternaRepository extends JpaRepository<Cisterna, Long> 
{
     Optional<Cisterna> findByLicencia(String licencia);

    Optional<Cisterna> findByLicenciaAndIdNot(String licencia, long id);
}
