package co.com.udea.tauri.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
public class BibliotecaRest {
	
	@Autowired
	private BibliotecaService bibliotecaService;
	
	@GetMapping(path = "/listarBiblioteca", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> listarBiblioteca(@RequestParam String categoria) {
		return new ResponseEntity<>(bibliotecaService.listarBibliotecasPorCategoria(categoria), HttpStatus.OK);
	}
	
	@PostMapping(path = "/guardarBiblioteca", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> guardarBiblioteca(@RequestBody BibliotecaDto bibliotecaDto) {
		return null;
	}
	
}
