package co.com.udea.tauri.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import co.com.udea.tauri.entities.Municipio;

@Repository
public interface MunicipioRepository extends JpaRepository<Municipio, Integer>{

	@Query ("SELECT m FROM Municipio m WHERE m.departamento.id = :departamento")
	public List<Municipio> findByIdDepartamento(Integer departamento);
}
