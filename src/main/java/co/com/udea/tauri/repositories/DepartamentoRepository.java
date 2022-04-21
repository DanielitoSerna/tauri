package co.com.udea.tauri.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.com.udea.tauri.entities.Departamento;

@Repository
public interface DepartamentoRepository extends JpaRepository<Departamento, Integer>{

	public List<Departamento> findAllByOrderByNombreDepartamentoAsc();
}
