package co.com.udea.tauri.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import co.com.udea.tauri.entities.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer>{

	public List<Usuario> findAllByOrderByFechaCreacionDesc();
	
	@Query("SELECT u FROM Usuario u WHERE u.correo = :usuario order by u.correo")
	public List<Usuario> findByUsuario(String usuario);
}
