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
	private static final Double KP_OF_WET_CONCENTRATE_CONSTANT_TRES = 0.02, CS3EBW_CONSTANT = 0.96,
			CS3EBW_CONSTANT_DOS = 0.851;
	private static final Double CONSTANT_TOTAL_DM_FEED = 0.372, CONSTANT_TOTAL_DM_FEED_DOS = 0.0968,
			CONSTANT_TOTAL_DM_FEED_TRES = 0.75, CONSTANT_TOTAL_DM_FEED_CUATRO = -0.192,
			CONSTANT_TOTAL_DM_FEED_CINCO = 3.67;
	private static final Double CONSTANT_SCURF_REQUIREMENT = 0.3, CONSTANT_SCURF_REQUIREMENT_DOS = 0.6;

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
		Double kpOfConcentrate = formatearDecimales(
				KP_OF_WET_CONCENTRATE_CONSTANT
						+ KP_OF_WET_CONCENTRATE_CONSTANT_DOS
								* (cmsActual / entradaDto.getPesoCorporal() * CONSTANT_CIEN)
						- KP_OF_WET_CONCENTRATE_CONSTANT_TRES * (cmsConcentrate / cmsActual * CONSTANT_CIEN),
				CANTIDAD_DECIMALES);
		System.out.println(kpOfConcentrate);
		Double cs3Ebw = 0.0;
		if ("Jersey".equals(entradaDto.getRaza())) {
			cs3Ebw = formatearDecimales(MW_JERSEY * CS3EBW_CONSTANT * CS3EBW_CONSTANT_DOS, CANTIDAD_DECIMALES);
		} else {
			cs3Ebw = formatearDecimales(MW_HOLSTEIN * CS3EBW_CONSTANT * CS3EBW_CONSTANT_DOS, CANTIDAD_DECIMALES);
		}
		System.out.println(cs3Ebw);
		Double bodyEnergy = 0.0;
		if (new Double(1).equals(entradaDto.getCondicionCorporal())) {

		}
		System.out.println(bodyEnergy);
		Double cw = formatearDecimales((18 + ((entradaDto.getDiasPrenez() - 190) * 0.665)) * (cbw / 45),
				CANTIDAD_DECIMALES);
		System.out.println(cw);

		// calculos modelo
		Double totalDmFeed = formatearDecimales(
				(CONSTANT_TOTAL_DM_FEED * fcm + CONSTANT_TOTAL_DM_FEED_DOS
						* Math.pow(entradaDto.getPesoCorporal(), CONSTANT_TOTAL_DM_FEED_TRES)
						* (1 - Math.exp(CONSTANT_TOTAL_DM_FEED_CUATRO * (wol + CONSTANT_TOTAL_DM_FEED_CINCO)))),
				CANTIDAD_DECIMALES);
		Double scurfRequirement = 0.0;
		Double urinaryRequirement = 0.0;
		if (entradaDto.getDiasPrenez() < 190) {
			scurfRequirement = formatearDecimales(
					CONSTANT_SCURF_REQUIREMENT
							* (Math.pow(entradaDto.getPesoCorporal(), CONSTANT_SCURF_REQUIREMENT_DOS)),
					CANTIDAD_DECIMALES);
			urinaryRequirement = formatearDecimales(4.1 * (Math.pow(entradaDto.getPesoCorporal(), 0.5)),
					CANTIDAD_DECIMALES);
		} else {
			scurfRequirement = formatearDecimales(
					CONSTANT_SCURF_REQUIREMENT
							* (Math.pow(entradaDto.getPesoCorporal() - cw, CONSTANT_SCURF_REQUIREMENT_DOS)),
					CANTIDAD_DECIMALES);
			urinaryRequirement = formatearDecimales(4.1 * (Math.pow(entradaDto.getPesoCorporal() - cw, 0.5)), CANTIDAD_DECIMALES);
		}
		
		Double metabolicFecalProteinReq = (cmsActual*1000*0.03-(0.5*((1260.12/0.8)-1260.12)));
		System.out.println(metabolicFecalProteinReq);
//		modeloDto.setActualDMI(cmsActual);
		modeloDto.setTotalDMFeed(totalDmFeed);
		modeloDto.setScurfRequirement(scurfRequirement);
		modeloDto.setUrinaryRequirement(urinaryRequirement);
		return modeloDto;
	}

	private static Double formatearDecimales(Double numero, Integer numeroDecimales) {
		return Math.round(numero * Math.pow(10, numeroDecimales)) / Math.pow(10, numeroDecimales);
	}

}
