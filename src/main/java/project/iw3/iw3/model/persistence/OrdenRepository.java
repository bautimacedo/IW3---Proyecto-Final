package project.iw3.iw3.model.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import project.iw3.iw3.model.Orden;
import project.iw3.iw3.model.enums.EstadoOrden;


@Repository
public interface OrdenRepository extends JpaRepository <Orden, Long> {
    
	
	Optional<Orden> findByPassword(int password);
	
	Optional<Orden> findByCamion_PatenteAndEstadoOrden(String patente, EstadoOrden status);

	Optional<Orden> findByNumeroOrden(Integer numeroOrden);

	
	
}
