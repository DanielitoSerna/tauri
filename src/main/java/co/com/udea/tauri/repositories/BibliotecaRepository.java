package co.com.udea.tauri.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import co.com.udea.tauri.entities.Biblioteca;

public interface BibliotecaRepository extends JpaRepository<Biblioteca, Integer> {
	
	@Query("SELECT b FROM Biblioteca b WHERE b.categoria = :categoria AND (b.usuario = :usuario or b.usuario = 'SISTEMA')")
	public List<Biblioteca> findByCategoria(String categoria, String usuario);
	
	@Query("SELECT DISTINCT b.categoria FROM Biblioteca b")
	public List<String> listarCategorias();
	
	@Query("SELECT DISTINCT b.tipo FROM Biblioteca b")
	public List<String> listarTipoAlimentos();

}
