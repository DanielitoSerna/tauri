package co.com.udea.tauri.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.com.udea.tauri.dtos.BibliotecaDto;
import co.com.udea.tauri.entities.Biblioteca;
import co.com.udea.tauri.repositories.BibliotecaRepository;
import co.com.udea.tauri.services.IBibliotecaService;

@Service
public class BibliotecaService implements IBibliotecaService {
	
	@Autowired
	private BibliotecaRepository bibliotecaRepository;

	@Override
	public List<BibliotecaDto> listarBibliotecasPorCategoriaUsuario(String categoria, String usuario) {
		List<Biblioteca> bibliotecas = null;
		if ((categoria != null && !categoria.isEmpty()) && (usuario != null && !usuario.isEmpty()) ) {
			bibliotecas =  bibliotecaRepository.findByCategoria(categoria, usuario);
		} else {
			bibliotecas = bibliotecaRepository.findAll();
		}
		return bibliotecas.stream().map(this::convertToDto).collect(Collectors.toList());
	}
	
	@Override
	public List<BibliotecaDto> listarTodosBiblioteca() {
		List<Biblioteca> bibliotecas = bibliotecaRepository.findAll();
		return bibliotecas.stream().map(this::convertToDto).collect(Collectors.toList());
	}
	
	@Override
	public BibliotecaDto guardarBiblioteca(BibliotecaDto bibliotecaDto) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private BibliotecaDto convertToDto(Biblioteca biblioteca) {
		ModelMapper modelMapper = new ModelMapper();
		BibliotecaDto bibliotecaDto = modelMapper.map(biblioteca, BibliotecaDto.class);
	    return bibliotecaDto;
	}

}
