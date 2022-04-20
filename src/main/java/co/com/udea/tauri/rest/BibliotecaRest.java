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

import co.com.udea.tauri.dtos.BibliotecaDto;
import co.com.udea.tauri.services.impl.BibliotecaService;

@RestController
@RequestMapping("api")
@CrossOrigin(origins = "*")
public class BibliotecaRest {
	
	@Autowired
	private BibliotecaService bibliotecaService;
	
	@GetMapping(path = "/listarBiblioteca", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> listarBiblioteca(@RequestParam String categoria, @RequestParam String usuario) {
		return new ResponseEntity<>(bibliotecaService.listarBibliotecasPorCategoriaUsuario(categoria, usuario), HttpStatus.OK);
	}
	
	@PostMapping(path = "/guardarBiblioteca", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> guardarBiblioteca(@RequestBody BibliotecaDto bibliotecaDto) {
		BibliotecaDto response = new BibliotecaDto();
		response = bibliotecaService.guardarBiblioteca(bibliotecaDto);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@GetMapping(path = "/listarTodosBiblioteca", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> listarTodosBiblioteca() {
		return new ResponseEntity<>(bibliotecaService.listarTodosBiblioteca(), HttpStatus.OK);
	}
	
	@GetMapping(path = "/listarCategorias", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> listarCategorias() {
		return new ResponseEntity<>(bibliotecaService.listarCategorias(), HttpStatus.OK);
	}
	
	@GetMapping(path = "/listarTipoAlimentos", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> listarTipoAlimentos() {
		return new ResponseEntity<>(bibliotecaService.listarTipoAlimentos(), HttpStatus.OK);
	}
	
	@PostMapping(path = "/eliminarBiblioteca", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> eliminarBiblioteca(@RequestParam Integer id) {
		return new ResponseEntity<>(bibliotecaService.eliminarBiblioteca(id), HttpStatus.OK);
	}
}
