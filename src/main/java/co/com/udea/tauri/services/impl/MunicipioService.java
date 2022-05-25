package co.com.udea.tauri.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.com.udea.tauri.dtos.DepartamentoDto;
import co.com.udea.tauri.dtos.MunicipioDto;
import co.com.udea.tauri.entities.Municipio;
import co.com.udea.tauri.repositories.MunicipioRepository;
import co.com.udea.tauri.services.IMunicipioService;

@Service
public class MunicipioService implements IMunicipioService{
	
	@Autowired
	private MunicipioRepository municipioRepository;

	@Override
	public List<MunicipioDto> obtenerMunicipios(Integer idDepartamento) {
		List<Municipio> municipios = municipioRepository.findByIdDepartamento(idDepartamento);
		return municipios.stream().map(this::convertToDto).collect(Collectors.toList());
	}
	
	private MunicipioDto convertToDto(Municipio municipio) {
		ModelMapper modelMapper = new ModelMapper();
		DepartamentoDto departamentoDto = modelMapper.map(municipio.getDepartamento(), DepartamentoDto.class);
		MunicipioDto municipioDto = modelMapper.map(municipio, MunicipioDto.class);
		municipioDto.setDepartamentoDto(departamentoDto);
	    return municipioDto;
	}

}
