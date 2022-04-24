package co.com.udea.tauri.services.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.com.udea.tauri.dtos.DietaDto;
import co.com.udea.tauri.entities.Biblioteca;
import co.com.udea.tauri.entities.Dieta;
import co.com.udea.tauri.entities.Entrada;
import co.com.udea.tauri.repositories.BibliotecaRepository;
import co.com.udea.tauri.repositories.DietaRepository;
import co.com.udea.tauri.repositories.EntradaRepository;
import co.com.udea.tauri.services.IDietaService;

@Service
public class DietaService implements IDietaService{
	
	@Autowired
	private DietaRepository dietaRepository;
	
	@Autowired
	private BibliotecaRepository bibliotecaRepository;
	
	@Autowired
	private EntradaRepository entradaRepository;

	@Override
	public DietaDto guardarDieta(DietaDto dietaDto) {
		Dieta dieta = convertToEntity(dietaDto);
		Dieta response = dietaRepository.save(dieta);
		return convertToDto(response);
	}
	
	@Override
	public String eliminarDieta(Integer idEntrada) {
		String mensaje = "";
		try  {
			List<Dieta> dietas = dietaRepository.findDietaByIdEntrada(idEntrada);
			for (Dieta dieta : dietas) {
				dietaRepository.delete(dieta);
			}
			mensaje = "dieta eliminada correctamente";
		} catch (Exception e) {
			mensaje = "error al eliminar la dieta";
		}
		return mensaje;
	}
	
	private DietaDto convertToDto(Dieta dieta) {
		ModelMapper modelMapper = new ModelMapper();
		DietaDto dietaDto = modelMapper.map(dieta, DietaDto.class);
	    return dietaDto;
	}
	
	private Dieta convertToEntity(DietaDto dietaDto) {
		ModelMapper modelMapper = new ModelMapper();
		Dieta dieta = modelMapper.map(dietaDto, Dieta.class);
		Biblioteca biblioteca = bibliotecaRepository.findById(dietaDto.getIdBiblioteca()).get();
		dieta.setIdBiblioteca(biblioteca);
		Entrada entrada = entradaRepository.findById(dietaDto.getIdEntrada()).get();
		dieta.setIdEntrada(entrada);
		return dieta;
	}

}
