package co.com.udea.tauri.services;

import java.util.List;

import co.com.udea.tauri.dtos.BibliotecaDto;

public interface IBibliotecaService {
	
	public List<BibliotecaDto> listarBibliotecasPorCategoriaUsuario(String categoria, String usuario);
	
	public List<BibliotecaDto> listarTodosBiblioteca();

}
