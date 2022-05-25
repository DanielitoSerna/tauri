package co.com.udea.tauri.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import co.com.udea.tauri.entities.Departamento;

@Repository
public interface DepartamentoRepository extends JpaRepository<Departamento, Integer>{
	
	@Query ("SELECT d FROM Departamento d WHERE d.pais.id = :pais order by d.nombreDepartamento")
	public List<Departamento> findByIdPais(Integer pais);
}
