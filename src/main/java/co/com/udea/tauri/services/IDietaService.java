package co.com.udea.tauri.services;

import java.util.List;

import co.com.udea.tauri.dtos.DietaDto;

public interface IDietaService {
	
	public DietaDto guardarDieta(DietaDto dietaDto);
	
	public String eliminarDieta(Integer idEntrada);
	
	public List<DietaDto> listarDieta(Integer idEntrada);

}
