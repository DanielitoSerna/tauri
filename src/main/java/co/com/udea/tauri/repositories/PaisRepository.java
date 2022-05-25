package co.com.udea.tauri.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.com.udea.tauri.entities.Pais;

@Repository
public interface PaisRepository extends JpaRepository<Pais, Integer>{

	public List<Pais> findAllByOrderByPaisAsc();
}
