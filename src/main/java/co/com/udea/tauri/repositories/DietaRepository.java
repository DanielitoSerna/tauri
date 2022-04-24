package co.com.udea.tauri.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import co.com.udea.tauri.entities.Dieta;

@Repository
public interface DietaRepository extends JpaRepository<Dieta, Integer>{
	
	@Query("SELECT d FROM Dieta d where d.idEntrada.id = :idEntrada")
	public List<Dieta> findDietaByIdEntrada(Integer idEntrada);

}
