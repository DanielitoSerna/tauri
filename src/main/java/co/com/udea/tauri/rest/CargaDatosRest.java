package co.com.udea.tauri.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.com.udea.tauri.services.impl.DepartamentoService;
import co.com.udea.tauri.services.impl.MunicipioService;
import co.com.udea.tauri.services.impl.PaisService;

@RestController
@RequestMapping("api")
@CrossOrigin(origins = "*")
public class CargaDatosRest {
	
	@Autowired
	private DepartamentoService departamentoService;
	
	@Autowired
	private PaisService paisService;
	
	@Autowired
	private MunicipioService municipioService;
	
	@GetMapping(path = "/paises", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> obtenerPaises() {
		return new ResponseEntity<>(paisService.listarPaises(), HttpStatus.OK);
	}
	
	@GetMapping(path = "/departamentos", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> obtenerDepartamentos(@RequestParam Integer idPais) {
		return new ResponseEntity<>(departamentoService.listarDepartamento(idPais), HttpStatus.OK);
	}
	
	@GetMapping(path = "/municipios", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> obtenerMunicipiosDepartamento(@RequestParam Integer idDepartamento) {
		return new ResponseEntity<>(municipioService.obtenerMunicipios(idDepartamento), HttpStatus.OK);
	}

}
