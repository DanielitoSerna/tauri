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

import co.com.udea.tauri.dtos.DietaDto;
import co.com.udea.tauri.services.impl.DietaService;

@RestController
@RequestMapping("api")
@CrossOrigin(origins = "*")
public class DietaRest {
	
	@Autowired
	private DietaService dietaService;

	@PostMapping(path = "/guardarDieta", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> guardarDieta(@RequestBody DietaDto dietaDto) {
		DietaDto response = new DietaDto();
		response = dietaService.guardarDieta(dietaDto);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PostMapping(path = "/eliminarDieta", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> eliminarDieta(@RequestParam Integer idEntrada) {
		return new ResponseEntity<>(dietaService.eliminarDieta(idEntrada), HttpStatus.OK);
	}
	
	@GetMapping(path = "/dietas", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> listarDieta(@RequestParam Integer idEntrada) {
		return new ResponseEntity<>(dietaService.listarDieta(idEntrada), HttpStatus.OK);
	}
}
