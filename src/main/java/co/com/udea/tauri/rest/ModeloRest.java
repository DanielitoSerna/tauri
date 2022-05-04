package co.com.udea.tauri.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.com.udea.tauri.dtos.DatoCalculoDto;
import co.com.udea.tauri.services.impl.ModeloService;

@RestController
@RequestMapping("api")
@CrossOrigin(origins = "*")
public class ModeloRest {

	@Autowired
	private ModeloService modeloService;

	@PostMapping(path = "/retornarModelo", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> retornarModelo(@RequestBody DatoCalculoDto datoCalculoDto) {
		return new ResponseEntity<>(
				modeloService.calcularModelo(datoCalculoDto.getEntradaDto(), datoCalculoDto.getDietaDtos()),
				HttpStatus.OK);
	}

}
