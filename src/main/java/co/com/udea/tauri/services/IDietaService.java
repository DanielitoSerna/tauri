package co.com.udea.tauri.services;

import co.com.udea.tauri.dtos.DietaDto;

public interface IDietaService {
	
	public DietaDto guardarDieta(DietaDto dietaDto);
	
	public String eliminarDieta(Integer idEntrada);

}
