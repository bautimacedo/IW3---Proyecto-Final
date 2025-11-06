package project.iw3.iw3.model.persistence;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import project.iw3.iw3.model.DetalleCarga;

public interface DetalleCargaRepository extends JpaRepository<DetalleCarga, Long> {
    List<DetalleCarga> findByOrdenId(Long ordenId);
}
