package co.com.udea.tauri.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.com.udea.tauri.dtos.DietaDto;
import co.com.udea.tauri.dtos.EntradaDto;
import co.com.udea.tauri.dtos.ModeloDto;
import co.com.udea.tauri.entities.Biblioteca;
import co.com.udea.tauri.repositories.BibliotecaRepository;
import co.com.udea.tauri.services.IModeloService;

@Service
public class ModeloService implements IModeloService {

	private static final Double MW_JERSEY = 454.0, MW_HOLSTEIN = 680.0, CBW_CONSTANT = 0.06275;
	private static final Double WOL_CONSTANT = 7.0, MILK_PROD_CONSTANT = 1.03, MILK_TRUE_PROTEIN_CONSTANT = 0.93;
	private static final Double FCM_CONSTANT_DOUBLE = 0.4, MILK_ENEG_CONSTANT = 0.0929, MILK_ENEG_CONSTANT_DOS = 0.0547;
	private static final Double MILK_ENEG_CONSTANT_TRES = 0.93, MILK_ENEG_CONSTANT_CUATRO = 0.192,
			MILK_ENEG_CONSTANT_CINCO = 0.0395;
	private static final Integer CANTIDAD_DECIMALES = 2, FCM_CONSTANT_INTEGER = 15, CONSTANT_CIEN = 100;
	private static final Double KP_OF_WET_FORAGE_CONSTANT = 3.054, KP_OF_WET_FORAGE_CONSTANT_DOS = 0.614;
	private static final Double KP_OF_WET_CONCENTRATE_CONSTANT = 2.904, KP_OF_WET_CONCENTRATE_CONSTANT_DOS = 1.375;
	private static final Double KP_OF_WET_CONCENTRATE_CONSTANT_TRES = 0.02;
	
	@Autowired
	private BibliotecaRepository bibliotecaRepository;

	@Override
	public ModeloDto calcularModelo(EntradaDto entradaDto, List<DietaDto> dietaDtos) {
		ModeloDto modeloDto = new ModeloDto();

		Double wol = formatearDecimales(entradaDto.getDiasLeche() / WOL_CONSTANT, CANTIDAD_DECIMALES);
		System.out.println(wol);
		Double milkProd = formatearDecimales(entradaDto.getProduccionLeche() * MILK_PROD_CONSTANT, CANTIDAD_DECIMALES);
		System.out.println(milkProd);
		Double milkTrueProtein = formatearDecimales(entradaDto.getProteinaCruda() * MILK_TRUE_PROTEIN_CONSTANT,
				CANTIDAD_DECIMALES);
		System.out.println(milkTrueProtein);
		Double fcm = formatearDecimales((FCM_CONSTANT_DOUBLE * milkProd
				+ FCM_CONSTANT_INTEGER * (milkProd * entradaDto.getGrasa() / CONSTANT_CIEN)), 2);
		System.out.println(fcm);
		Double milkEneg = 0.0;
		if (entradaDto.getLactosa() == 0) {
			milkEneg = formatearDecimales((MILK_ENEG_CONSTANT * entradaDto.getGrasa())
					+ (MILK_ENEG_CONSTANT_DOS * (milkTrueProtein / MILK_ENEG_CONSTANT_TRES))
					+ MILK_ENEG_CONSTANT_CUATRO, 2);
		} else {
			if (entradaDto.getLactosa() > 0) {
				milkEneg = formatearDecimales((MILK_ENEG_CONSTANT * entradaDto.getGrasa())
						+ (MILK_ENEG_CONSTANT_DOS * (milkTrueProtein / MILK_ENEG_CONSTANT_TRES))
						+ (MILK_ENEG_CONSTANT_CINCO * entradaDto.getLactosa()), 2);
			}
		}
		System.out.println(milkEneg);
		Double yprotn = formatearDecimales((milkProd * (milkTrueProtein / CONSTANT_CIEN)), 2);
		System.out.println(yprotn);
		Double cbw = 0.0;
		if ("Jersey".equals(entradaDto.getRaza())) {
			cbw = MW_JERSEY * CBW_CONSTANT;
		} else {
			cbw = MW_HOLSTEIN * CBW_CONSTANT;
		}
		System.out.println(cbw);
		Double cmsActual = 0.0;
		Double cmsConcentrate = 0.0;
		for (DietaDto dietaDto : dietaDtos) {
			cmsActual = cmsActual + dietaDto.getCantidad();
			Biblioteca biblioteca = bibliotecaRepository.findById(dietaDto.getIdBiblioteca()).get();
			if (biblioteca != null) {
				if ("Concentrado".equals(biblioteca.getTipo())) {
					cmsConcentrate = cmsConcentrate + dietaDto.getCantidad();
				}
			}
		}
		Double kpOfWetForage = formatearDecimales(
				KP_OF_WET_FORAGE_CONSTANT
						+ KP_OF_WET_FORAGE_CONSTANT_DOS * (cmsActual / entradaDto.getPesoCorporal() * CONSTANT_CIEN),
				CANTIDAD_DECIMALES);
		System.out.println(kpOfWetForage);
		Double kpOfConcentrate = formatearDecimales(KP_OF_WET_CONCENTRATE_CONSTANT
				+ KP_OF_WET_CONCENTRATE_CONSTANT_DOS * (cmsActual / entradaDto.getPesoCorporal() * CONSTANT_CIEN)
				- KP_OF_WET_CONCENTRATE_CONSTANT_TRES*(cmsConcentrate/cmsActual*CONSTANT_CIEN), CANTIDAD_DECIMALES);
		System.out.println(kpOfConcentrate);
		return modeloDto;
	}

	private static Double formatearDecimales(Double numero, Integer numeroDecimales) {
		return Math.round(numero * Math.pow(10, numeroDecimales)) / Math.pow(10, numeroDecimales);
	}

}
