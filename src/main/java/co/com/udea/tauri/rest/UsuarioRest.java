package co.com.udea.tauri.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.com.udea.tauri.dtos.UsuarioDto;
import co.com.udea.tauri.services.IUsuarioService;

@RestController
@RequestMapping("api")
@CrossOrigin(origins = "*")
public class UsuarioRest {
	
	@Autowired
	private IUsuarioService usuarioService;
	
	@GetMapping(path = "/listarUsuarios", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> listarUsuarios() {
		return new ResponseEntity<>(usuarioService.listarUsuarios(), HttpStatus.OK);
	}
	
	@PostMapping(path = "/guardarUsuario", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> guardarUsuario(@RequestBody UsuarioDto usuarioDto) {
		UsuarioDto response = usuarioService.guardarUsuario(usuarioDto);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@GetMapping(path = "/listarUsuario", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> listarUsuario(@RequestParam String usuario) {
		return new ResponseEntity<>(usuarioService.listarUsuario(usuario), HttpStatus.OK);
	}
}
