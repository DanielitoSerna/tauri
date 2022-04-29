package co.com.udea.tauri.services;

import java.util.List;

import co.com.udea.tauri.dtos.DietaDto;
import co.com.udea.tauri.dtos.EntradaDto;
import co.com.udea.tauri.dtos.ModeloDto;

public interface IModeloService {
	
	public ModeloDto calcularModelo(EntradaDto entradaDto, List<DietaDto> dietaDtos);

}
