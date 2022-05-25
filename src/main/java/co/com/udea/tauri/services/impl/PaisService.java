package co.com.udea.tauri.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.com.udea.tauri.dtos.PaisDto;
import co.com.udea.tauri.entities.Pais;
import co.com.udea.tauri.repositories.PaisRepository;
import co.com.udea.tauri.services.IPaisService;

@Service
public class PaisService implements IPaisService {
	
	@Autowired
	private PaisRepository paisRepository;
	
	@Override
	public List<PaisDto> listarPaises() {
		List<Pais> paises = paisRepository.findAllByOrderByPaisAsc();
		return paises.stream().map(this::convertToDto).collect(Collectors.toList());
	}
	
	private PaisDto convertToDto(Pais pais) {
		ModelMapper modelMapper = new ModelMapper();
	    return modelMapper.map(pais, PaisDto.class);
	}

}
