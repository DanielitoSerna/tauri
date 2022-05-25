package co.com.udea.tauri.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.com.udea.tauri.dtos.DepartamentoDto;
import co.com.udea.tauri.entities.Departamento;
import co.com.udea.tauri.repositories.DepartamentoRepository;
import co.com.udea.tauri.services.IDepartamentoService;

@Service
public class DepartamentoService implements IDepartamentoService {
	
	@Autowired
	private DepartamentoRepository departamentoRepository;
	
	@Override
	public List<DepartamentoDto> listarDepartamento(Integer pais) {
		List<Departamento> departamentos = departamentoRepository.findByIdPais(pais);
		return departamentos.stream().map(this::convertToDto).collect(Collectors.toList());
	}
	
	private DepartamentoDto convertToDto(Departamento departamento) {
		ModelMapper modelMapper = new ModelMapper();
	    return modelMapper.map(departamento, DepartamentoDto.class);
	}

}
