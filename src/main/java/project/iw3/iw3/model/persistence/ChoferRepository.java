package project.iw3.iw3.model.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import project.iw3.iw3.model.Chofer;

@Repository
public interface ChoferRepository extends JpaRepository<Chofer, Long> {
    
    boolean existsByDni(String dni);

    Optional<Chofer> findByDni(String dni);

    Optional<Chofer> findByDniAndIdNot(String dni, long id);
}
