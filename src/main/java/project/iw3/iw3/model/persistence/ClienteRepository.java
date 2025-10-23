package project.iw3.iw3.model.persistence;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import project.iw3.iw3.model.Cliente;
//Se encarga de hablar con la BD para guardar entidades, buscarlas, borrarlas,etc.
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    
    Optional<Cliente> findByNombreEmpresa(String nombreEmpresa);

    Optional<Cliente> findByNombreEmpresaAndIdNot(String nombreEmpresa, long id);
    //Este se usa cuando queres edidtar datos de un cliente y queres validar q el nuevo
    //nombre de empresa no exista en otro cliente
}
