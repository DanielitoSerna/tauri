package co.com.udea.tauri.dtos;

import java.util.List;

public class DatoCalculoDto {
	
	private EntradaDto entradaDto;
	
	private List<DietaDto> dietaDtos;

	public EntradaDto getEntradaDto() {
		return entradaDto;
	}

	public void setEntradaDto(EntradaDto entradaDto) {
		this.entradaDto = entradaDto;
	}

	public List<DietaDto> getDietaDtos() {
		return dietaDtos;
	}

	public void setDietaDtos(List<DietaDto> dietaDtos) {
		this.dietaDtos = dietaDtos;
	}
	
}
