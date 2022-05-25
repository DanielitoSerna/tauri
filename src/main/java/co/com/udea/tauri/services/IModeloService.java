package co.com.udea.tauri.services;

import java.util.List;

import co.com.udea.tauri.dtos.BalanceDto;
import co.com.udea.tauri.dtos.ConsumoMateriaSecaDto;
import co.com.udea.tauri.dtos.DietaDto;
import co.com.udea.tauri.dtos.EmisionGeiDto;
import co.com.udea.tauri.dtos.EntradaDto;
import co.com.udea.tauri.dtos.ModeloDto;
import co.com.udea.tauri.dtos.RelacionBeneficioCostoDto;

public interface IModeloService {
	
	public ModeloDto calcularModelo(EntradaDto entradaDto, List<DietaDto> dietaDtos);
	
	public RelacionBeneficioCostoDto calcularRelacionBeneficio(EntradaDto entradaDto, List<DietaDto>dietaDtos );
	
	public EmisionGeiDto calcularEmisionGei(EntradaDto entradaDto, List<DietaDto> dietaDtos);
	
	public ConsumoMateriaSecaDto calcularConsumoMateriaSecaPredico(EntradaDto entradaDto);
	
	public BalanceDto calcularBalance(EntradaDto entradaDto, List<DietaDto> dietaDtos);

}
