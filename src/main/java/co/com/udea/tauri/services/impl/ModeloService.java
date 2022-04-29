package co.com.udea.tauri.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import co.com.udea.tauri.dtos.DietaDto;
import co.com.udea.tauri.dtos.EntradaDto;
import co.com.udea.tauri.dtos.ModeloDto;
import co.com.udea.tauri.services.IModeloService;

@Service
public class ModeloService implements IModeloService {
	
	private static final Double MW_JERSEY = 454.0, MW_HOLSTEIN = 680.0;
	
	@Override
	public ModeloDto calcularModelo(EntradaDto entradaDto, List<DietaDto> dietaDtos ) {
		ModeloDto modeloDto = new ModeloDto();
		
		Double wol = formatearDecimales(120.0/7.0, 2);
		System.out.println( wol );
		Double milkProd = formatearDecimales(30.0*1.03, 2);
		System.out.println( milkProd );
//		Double fcm = (0.4*30.9+15*(30.9*3.5/100));
		return modeloDto;
	}
	
	private static Double formatearDecimales(Double numero, Integer numeroDecimales) {
		return Math.round(numero * Math.pow(10, numeroDecimales)) / Math.pow(10, numeroDecimales);
		}

}
