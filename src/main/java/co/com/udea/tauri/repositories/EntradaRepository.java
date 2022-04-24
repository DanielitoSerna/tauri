package co.com.udea.tauri.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.com.udea.tauri.entities.Entrada;

@Repository
public interface EntradaRepository extends JpaRepository<Entrada, Integer>{

	public List<Entrada> findByUsuario(String usuario);
}
