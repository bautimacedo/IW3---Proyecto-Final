package project.iw3.iw3.model.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import project.iw3.iw3.model.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long>{

	boolean existsByNombre(String nombre);
	
	Optional<Producto> findByNombre(String nombre);

	Optional<Producto> findByNombreAndIdNot(String nombre, long id);
	/*
	@Query(value = "SELECT count(*) FROM products where id_category=?", nativeQuery = true)
	public Integer countProductsByCategory(long idCategory);
	
	@Transactional
    @Modifying
    @Query(value = "UPDATE products SET stock=? WHERE id=?", nativeQuery = true)
	//public int setStock(boolean stock, long idProduct);
    */

}