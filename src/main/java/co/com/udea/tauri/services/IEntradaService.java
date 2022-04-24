package co.com.udea.tauri.services;

import java.util.List;

import co.com.udea.tauri.dtos.EntradaDto;

public interface IEntradaService {
	
	public EntradaDto guardarEntrada(EntradaDto entradaDto);
	
	public List<EntradaDto> listarEntradas(String usuario);

}
