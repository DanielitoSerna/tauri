package co.com.udea.tauri.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import co.com.udea.tauri.entities.Biblioteca;

public interface BibliotecaRepository extends JpaRepository<Biblioteca, Integer> {
	
	public List<Biblioteca> findByCategoria(String categoria);

}
