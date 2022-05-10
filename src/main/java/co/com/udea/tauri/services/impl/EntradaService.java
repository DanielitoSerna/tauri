package co.com.udea.tauri.services.impl;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.com.udea.tauri.dtos.EntradaDto;
import co.com.udea.tauri.entities.Departamento;
import co.com.udea.tauri.entities.Entrada;
import co.com.udea.tauri.entities.Municipio;
import co.com.udea.tauri.repositories.DepartamentoRepository;
import co.com.udea.tauri.repositories.EntradaRepository;
import co.com.udea.tauri.repositories.MunicipioRepository;
import co.com.udea.tauri.services.IEntradaService;

@Service
public class EntradaService implements IEntradaService{
	
	@Autowired
	private EntradaRepository entradaRepository;
	
	@Autowired
	private DepartamentoRepository departamentoRepository;
	
	@Autowired
	private MunicipioRepository municipioRepository;

	@Override
	public EntradaDto guardarEntrada(EntradaDto entradaDto) {
		Entrada entrada = convertToEntity(entradaDto);
		Entrada entradaResponse = entradaRepository.save(entrada);
		return convertToDto(entradaResponse);
	}
	
	@Override
	public EntradaDto getEntrada(int idEntrada) {
		return convertToDto(entradaRepository.findById(idEntrada).get());
	}
	
	
	@Override
	public List<EntradaDto> listarEntradas(String usuario) {
		List<Entrada> entradas = entradaRepository.findByUsuario(usuario);
		return entradas.stream().map(this::convertToDto).collect(Collectors.toList());
	}
	
	private EntradaDto convertToDto(Entrada entrada) {
		ModelMapper modelMapper = new ModelMapper();
		EntradaDto entradaDto = modelMapper.map(entrada, EntradaDto.class);
		entradaDto.setDepartamentoDto(entrada.getDepartamento().getId());
		entradaDto.setMunicipioDto(entrada.getMunicipio().getId());
	    return entradaDto;
	}
	
	private Entrada convertToEntity(EntradaDto entradaDto) {
		ModelMapper modelMapper = new ModelMapper();
		entradaDto.setFechaCreacion(new Date());
		Departamento departamento = departamentoRepository.findById(entradaDto.getDepartamentoDto()).get();
		Municipio municipio = municipioRepository.getById(entradaDto.getMunicipioDto());
		Entrada entrada = modelMapper.map(entradaDto, Entrada.class);
		entrada.setDepartamento(departamento);
		entrada.setMunicipio(municipio);
		return entrada;
	}

}
