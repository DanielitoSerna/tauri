package co.com.udea.tauri.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.com.udea.tauri.dtos.UsuarioDto;
import co.com.udea.tauri.entities.Usuario;
import co.com.udea.tauri.repositories.UsuarioRepository;
import co.com.udea.tauri.services.IUsuarioService;

@Service
public class UsuarioServices implements IUsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Override
	public List<UsuarioDto> listarUsuarios() {
		return usuarioRepository.findAllByOrderByFechaCreacionDesc().stream().map(this::convertToDto)
				.collect(Collectors.toList());
	}

	@Override
	public UsuarioDto guardarUsuario(UsuarioDto usuarioDto) {
		Usuario usuario = convertToEntity(usuarioDto);
		return convertToDto(usuarioRepository.save(usuario));
	}

	private UsuarioDto convertToDto(Usuario usuario) {
		ModelMapper modelMapper = new ModelMapper();
		return modelMapper.map(usuario, UsuarioDto.class);
	}

	private Usuario convertToEntity(UsuarioDto usuarioDto) {
		ModelMapper modelMapper = new ModelMapper();
		return modelMapper.map(usuarioDto, Usuario.class);
	}

	@Override
	public List<UsuarioDto> listarUsuario(String usuario) {
		return usuarioRepository.findByUsuario(usuario).stream().map(this::convertToDto)
				.collect(Collectors.toList());
	}

}
