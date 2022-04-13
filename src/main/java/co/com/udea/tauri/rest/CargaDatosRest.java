package co.com.udea.tauri.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.com.udea.tauri.services.impl.DepartamentoService;
import co.com.udea.tauri.services.impl.MunicipioService;

@RestController
@RequestMapping("api")
public class CargaDatosRest {
	
	@Autowired
	private DepartamentoService departamentoService;
	
	@Autowired
	private MunicipioService municipioService;
	
	@GetMapping(path = "/departamentos", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> obtenerDepartamentos() {
		return new ResponseEntity<>(departamentoService.listarDepartamento(), HttpStatus.OK);
	}
	
	@GetMapping(path = "/municipios", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> obtenerMunicipiosDepartamento(@RequestParam Integer idDepartamento) {
		return new ResponseEntity<>(municipioService.obtenerMunicipios(idDepartamento), HttpStatus.OK);
	}

}
