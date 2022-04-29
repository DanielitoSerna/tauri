package co.com.udea.tauri.services;

import java.util.List;

import co.com.udea.tauri.dtos.UsuarioDto;

public interface IUsuarioService {
	
	public List<UsuarioDto> listarUsuarios();
	
	public UsuarioDto guardarUsuario(UsuarioDto usuarioDto);

	public List<UsuarioDto> listarUsuario(String usuario);
}
