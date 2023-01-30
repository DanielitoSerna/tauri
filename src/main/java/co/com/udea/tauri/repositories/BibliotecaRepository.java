package co.com.udea.tauri.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import co.com.udea.tauri.entities.Biblioteca;

@Repository
public interface BibliotecaRepository extends JpaRepository<Biblioteca, Integer> {
	
	@Query("SELECT b FROM Biblioteca b WHERE b.usuario = :usuario or b.usuario = 'SISTEMA' order by b.nombre")
	public List<Biblioteca> findByUsuario(String usuario);
	
	@Query("SELECT DISTINCT b.categoria FROM Biblioteca b order by b.categoria")
	public List<String> listarCategorias();
	
	@Query("SELECT DISTINCT b.tipo FROM Biblioteca b")
	public List<String> listarTipoAlimentos();

}
