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

import co.com.udea.tauri.dtos.EntradaDto;
import co.com.udea.tauri.services.impl.EntradaService;

@RestController
@RequestMapping("api")
@CrossOrigin(origins = "*")
public class EntradaRest {
	
	@Autowired
	private EntradaService entradaService;
	
	@PostMapping(path = "/guardarEntrada", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> guardarEntrada(@RequestBody EntradaDto entradaDto) {
		EntradaDto response = new EntradaDto();
		response = entradaService.guardarEntrada(entradaDto);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@GetMapping(path = "/entradas", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> listarEntradas(@RequestParam String usuario) {
		return new ResponseEntity<>(entradaService.listarEntradas(usuario), HttpStatus.OK);
	}

}
