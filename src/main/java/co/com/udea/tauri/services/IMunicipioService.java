package co.com.udea.tauri.services;

import java.util.List;

import co.com.udea.tauri.dtos.MunicipioDto;

public interface IMunicipioService {

	public List<MunicipioDto> obtenerMunicipios(Integer idDepartamento);
}
