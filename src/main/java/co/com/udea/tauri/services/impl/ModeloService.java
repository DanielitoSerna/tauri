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
				+ FCM_CONSTANT_INTEGER * (milkProd * entradaDto.getGrasa() / CONSTANT_CIEN)), CANTIDAD_DECIMALES);
		System.out.println(fcm);
		Double milkEneg = 0.0;
		if (entradaDto.getLactosa() == 0) {
			milkEneg = formatearDecimales((MILK_ENEG_CONSTANT * entradaDto.getGrasa())
					+ (MILK_ENEG_CONSTANT_DOS * (milkTrueProtein / MILK_ENEG_CONSTANT_TRES))
					+ MILK_ENEG_CONSTANT_CUATRO, CANTIDAD_DECIMALES);
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
		Double tdn = 0.0;
		Double sumaTdn = 0.0;
		Double cpIntake = 0.0;
		for (DietaDto dietaDto : dietaDtos) {
			cmsActual = cmsActual + dietaDto.getCantidad();
			Biblioteca biblioteca = bibliotecaRepository.findById(dietaDto.getIdBiblioteca()).get();
			if (biblioteca != null) {
				tdn = formatearDecimales((dietaDto.getCantidad() * (biblioteca.getEd() / 0.04409)) / CONSTANT_CIEN,
						CANTIDAD_DECIMALES);
				sumaTdn = sumaTdn + tdn;
				if ("Concentrado".equals(biblioteca.getTipo())) {
					cmsConcentrate = cmsConcentrate + dietaDto.getCantidad();
				}
			}
		}
		Double tdnOriginal = formatearDecimales(((sumaTdn / cmsActual) * CONSTANT_CIEN), CANTIDAD_DECIMALES);
		Double nel = formatearDecimales((tdnOriginal * 0.0245) - 0.12, CANTIDAD_DECIMALES);

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

		Double totalDmFeed = formatearDecimales(
				(CONSTANT_TOTAL_DM_FEED * fcm + CONSTANT_TOTAL_DM_FEED_DOS
						* Math.pow(entradaDto.getPesoCorporal(), CONSTANT_TOTAL_DM_FEED_TRES)
						* (1 - Math.exp(CONSTANT_TOTAL_DM_FEED_CUATRO * (wol + CONSTANT_TOTAL_DM_FEED_CINCO)))),
				CANTIDAD_DECIMALES);
		Double scurfRequirement = 0.0;
		Double urinaryRequirement = 0.0;
		Double neMaint = 0.0;
		Double porcentajeRdp = 0.0;
		Double rdp = 0.0, sumaRdp = 0.0;
		for (DietaDto dietaDto : dietaDtos) {
			Biblioteca biblioteca = bibliotecaRepository.findById(dietaDto.getIdBiblioteca()).get();
			if (biblioteca != null) {
				cpIntake = formatearDecimales(dietaDto.getCantidad() * biblioteca.getFdn() / 100 * 1000,
						CANTIDAD_DECIMALES);
				if (new Double(0).equals(dietaDto.getCantidad())) {
					porcentajeRdp = 0.0;
				}
				if ("Forraje".equals(biblioteca.getTipo())) {
					porcentajeRdp = formatearDecimales(
							biblioteca.getFraccionA() + (biblioteca.getFraccionB()
									* (biblioteca.getKdFraccionB() / (biblioteca.getKdFraccionB() + kpOfWetForage))),
							CANTIDAD_DECIMALES);
				} else {
					if ("Concentrado".equals(biblioteca.getTipo())) {
						porcentajeRdp = formatearDecimales(biblioteca.getFraccionA() + (biblioteca.getFraccionB()
								* (biblioteca.getKdFraccionB() / (biblioteca.getKdFraccionB() + kpOfConcentrate))),
								CANTIDAD_DECIMALES);
					}
				}
			}
			rdp = cpIntake * porcentajeRdp / 100;
			sumaRdp = sumaRdp + rdp;
		}

		Double neMaintEnergy = 0.0;
		Double pregnancyCalcium = 0.0;
		Double pregnancyPhosphorous = 0.0;
		Double mgPregnancy = 0.0;
		Double kPregnancy = 0.0;
		if (entradaDto.getDiasPrenez() < 190) {
			scurfRequirement = formatearDecimales(
					CONSTANT_SCURF_REQUIREMENT
							* (Math.pow(entradaDto.getPesoCorporal(), CONSTANT_SCURF_REQUIREMENT_DOS)),
					CANTIDAD_DECIMALES);
			urinaryRequirement = formatearDecimales(4.1 * (Math.pow(entradaDto.getPesoCorporal(), 0.5)),
					CANTIDAD_DECIMALES);
			if (new Double(0).equals(entradaDto.getDistancia()) || entradaDto.getDistancia() == null) {
				neMaint = formatearDecimales(((Math.pow(entradaDto.getPesoCorporal(), 0.75) * 0.08)),
						CANTIDAD_DECIMALES);

			} else {
				if (entradaDto.getTipografia() != null && entradaDto.getTipografia().equals("Plana")) {
					neMaint = formatearDecimales(((Math.pow(entradaDto.getPesoCorporal(), 0.75) * 0.08))
							+ ((((entradaDto.getDistancia() / 1000) * entradaDto.getNumeroViajes())
									* (0.00045 * entradaDto.getPesoCorporal()))
									+ (0.0012 * entradaDto.getPesoCorporal())),
							CANTIDAD_DECIMALES);

				} else {
					neMaint = formatearDecimales(
							((Math.pow(entradaDto.getPesoCorporal(), 0.75) * 0.08))
									+ ((((entradaDto.getDistancia() / 1000) * entradaDto.getNumeroViajes())
											* (0.00045 * entradaDto.getPesoCorporal()))
											+ (0.0012 * entradaDto.getPesoCorporal()))
									+ (0.006 * entradaDto.getPesoCorporal()),
							CANTIDAD_DECIMALES);
				}
			}
			neMaintEnergy = neMaint;
			pregnancyCalcium = 0.0;
			pregnancyPhosphorous = 0.0;
			mgPregnancy = 0.0;
			kPregnancy = 0.0;
		} else {
			scurfRequirement = formatearDecimales(
					CONSTANT_SCURF_REQUIREMENT
							* (Math.pow(entradaDto.getPesoCorporal() - cw, CONSTANT_SCURF_REQUIREMENT_DOS)),
					CANTIDAD_DECIMALES);
			urinaryRequirement = formatearDecimales(4.1 * (Math.pow(entradaDto.getPesoCorporal() - cw, 0.5)),
					CANTIDAD_DECIMALES);
			if (new Double(0).equals(entradaDto.getDistancia()) || entradaDto.getDistancia() == null) {
				neMaint = formatearDecimales(((Math.pow(entradaDto.getPesoCorporal() - cw, 0.75) * 0.08)),
						CANTIDAD_DECIMALES);
			} else {
				if (entradaDto.getTipografia() != null && entradaDto.getTipografia().equals("Plana")) {
					neMaint = formatearDecimales(((Math.pow(entradaDto.getPesoCorporal() - cw, 0.75) * 0.08))
							+ ((((entradaDto.getDistancia() / 1000) * entradaDto.getNumeroViajes())
									* (0.00045 * entradaDto.getPesoCorporal()))
									+ (0.0012 * entradaDto.getPesoCorporal())),
							CANTIDAD_DECIMALES);
				} else {
					neMaint = formatearDecimales(
							((Math.pow(entradaDto.getPesoCorporal() - cw, 0.75) * 0.08))
									+ ((((entradaDto.getDistancia() / 1000) * entradaDto.getNumeroViajes())
											* (0.00045 * entradaDto.getPesoCorporal()))
											+ (0.0012 * entradaDto.getPesoCorporal()))
									+ (0.006 * entradaDto.getPesoCorporal()),
							CANTIDAD_DECIMALES);
				}
			}
			neMaintEnergy = neMaint;
			pregnancyCalcium = formatearDecimales(
					0.02456 * Math.exp((0.05581 - (0.00007 * entradaDto.getDiasPrenez())) * entradaDto.getDiasPrenez())
							- 0.02456 * Math.exp((0.05581 - (0.00007 * (entradaDto.getDiasPrenez() - 1)))
									* (entradaDto.getDiasPrenez() - 1)),
					CANTIDAD_DECIMALES);
			pregnancyPhosphorous = 0.02743
					* Math.exp(((0.05527 - (0.000075 * entradaDto.getDiasPrenez())) * entradaDto.getDiasPrenez()))
					- 0.02743 * Math.exp(((0.05527 - (0.000075 * (entradaDto.getDiasPrenez() - 1)))
							* (entradaDto.getDiasPrenez() - 1)));
			mgPregnancy = 0.33;
			kPregnancy = 1.027;
		}

		Double dmiMaintenanceLevel = neMaint / nel;
		Double intakeAboveMaintenance = cmsActual / dmiMaintenanceLevel;
		Double tdnActX = formatearDecimales((tdnOriginal - ((0.18 * tdnOriginal) - 10.3) * intakeAboveMaintenance - 1),
				CANTIDAD_DECIMALES);
		Double nelAdjusted = formatearDecimales((tdnActX * 0.0245) - 0.12, CANTIDAD_DECIMALES);

		Double mcpFromTdnDiscounted = formatearDecimales((tdnActX / 100) * cmsActual * 130, CANTIDAD_DECIMALES);
		Double mcpFromTdnDiscountedRdp = sumaRdp * 0.85;
		Double mcpAdjust = 0.0;
		if (mcpFromTdnDiscounted > mcpFromTdnDiscountedRdp) {
			mcpAdjust = formatearDecimales(mcpFromTdnDiscountedRdp, CANTIDAD_DECIMALES);
		} else {
			mcpAdjust = mcpFromTdnDiscounted;
		}

		Double mpBact = mcpAdjust * 0.64;

		Double metabolicFecalProteinReq = formatearDecimales(
				(cmsActual * 1000 * 0.03 - (0.5 * ((mpBact / 0.8) - mpBact))), CANTIDAD_DECIMALES);
		Double mpEndoungeosRequirement = formatearDecimales((11.8 * cmsActual * 0.4) / 0.67, CANTIDAD_DECIMALES);

		Double mpMaint = formatearDecimales(
				scurfRequirement + urinaryRequirement + metabolicFecalProteinReq + mpEndoungeosRequirement,
				CANTIDAD_DECIMALES);

		Double mpLact = formatearDecimales((yprotn / 0.67) * 1000, CANTIDAD_DECIMALES);

		Double mpPreg = formatearDecimales((((0.69 * entradaDto.getDiasPrenez()) - 69.2) * (cbw / 45)) / 0.33,
				CANTIDAD_DECIMALES);

		Double meanTargetSbw = formatearDecimales(entradaDto.getPesoCorporal() * 0.96, CANTIDAD_DECIMALES);
		Double eqsbw = 0.0;
		if ("Jersey".equals(entradaDto.getRaza())) {
			eqsbw = formatearDecimales(meanTargetSbw * (478 / (MW_JERSEY * 0.96)), CANTIDAD_DECIMALES);
		} else {
			eqsbw = formatearDecimales(meanTargetSbw * (478 / (MW_HOLSTEIN * 0.96)), CANTIDAD_DECIMALES);
		}

		Double eqebw = formatearDecimales(eqsbw * 0.891, CANTIDAD_DECIMALES);
		Double eqebwExp = formatearDecimales(Math.pow(eqebw, 0.75), CANTIDAD_DECIMALES);
		Double swg = formatearDecimales(entradaDto.getGananciaPeso() * 0.96, CANTIDAD_DECIMALES);
		Double eqebg = formatearDecimales(swg * 0.956, CANTIDAD_DECIMALES);
		Double eqebgExp = formatearDecimales(Math.pow(eqebg, 1.097), CANTIDAD_DECIMALES);
		Double re = formatearDecimales(0.0635 * eqebwExp * eqebgExp, CANTIDAD_DECIMALES);

		Double npg = formatearDecimales(swg * (268 - (29.4 * (re / swg))), CANTIDAD_DECIMALES);
		Double effmpNpg = formatearDecimales((83.4 - (0.114 * eqsbw)) / 100, CANTIDAD_DECIMALES);

		Double growht = 0.0;
		Double fecalPhosphorous = 0.0;
		if (entradaDto.getNumeroParto() <= 2) {
			growht = re;
			fecalPhosphorous = formatearDecimales(0.8 * cmsActual, CANTIDAD_DECIMALES);
		} else {
			growht = 0.0;
			fecalPhosphorous = formatearDecimales(1 * cmsActual, CANTIDAD_DECIMALES);
		}

		Double yEn = formatearDecimales(fcm * milkProd, CANTIDAD_DECIMALES);

		Double nePreg = formatearDecimales(
				0.64 * ((((2 * 0.00159 * entradaDto.getDiasPrenez()) - 0.0352) * (cbw / 45)) / 0.14),
				CANTIDAD_DECIMALES);

		Double totalNeRequirement = formatearDecimales(neMaintEnergy + yEn + nePreg + growht, CANTIDAD_DECIMALES);

		Double fecalCalcium = 0.0;
		Double lactationPhosphorous = 0.0;
		Double kFecal = 0.0;
		Double kLactation = 0.0;
		Double mgLactation = 0.0;
		Double lactationCalcium = 0.0;
		
		if (entradaDto.getDiasLeche() > 0) {
			fecalCalcium = formatearDecimales(3.1 * (entradaDto.getPesoCorporal() / 100), CANTIDAD_DECIMALES);
			lactationPhosphorous = formatearDecimales(0.9*milkProd, CANTIDAD_DECIMALES);
			kFecal = formatearDecimales(6.1 * cmsActual, CANTIDAD_DECIMALES);
			kLactation = formatearDecimales(0.15 * milkProd, CANTIDAD_DECIMALES);
			mgLactation = formatearDecimales(milkProd * 0.15, CANTIDAD_DECIMALES);
			if ("Jersey".equals(entradaDto.getRaza())) {
				lactationCalcium = formatearDecimales(1.45 * milkProd, CANTIDAD_DECIMALES);
			} else {
				lactationCalcium = formatearDecimales(1.22 * milkProd, CANTIDAD_DECIMALES);
			}
		} else {
			if (entradaDto.getDiasLeche() == 0) {
				fecalCalcium = formatearDecimales(1.54 * (entradaDto.getPesoCorporal() / 100), CANTIDAD_DECIMALES);
				kFecal = formatearDecimales(2.6 * cmsActual, CANTIDAD_DECIMALES);
				kLactation = 0.0;
				lactationPhosphorous = 0.0;
				mgLactation = 0.0;
				lactationCalcium = 0.0;
			}
		}

		Double urinaryCalcium = formatearDecimales(0.08 * (entradaDto.getPesoCorporal() / 100), CANTIDAD_DECIMALES);

		Double growthCalcium = 0.0;
		Double mpGrowth = 0.0;
		if (entradaDto.getGananciaPeso() > 0) {
			if ("Jersey".equals(entradaDto.getRaza())) {
				growthCalcium = formatearDecimales(
						(9.83 * (Math.pow(MW_JERSEY, 0.22)) * (Math.pow(entradaDto.getPesoCorporal(), -0.22)))
								* (entradaDto.getGananciaPeso()),
						CANTIDAD_DECIMALES);
			} else {
				growthCalcium = formatearDecimales(
						(9.83 * (Math.pow(MW_HOLSTEIN, 0.22)) * (Math.pow(entradaDto.getPesoCorporal(), -0.22)))
								* (entradaDto.getGananciaPeso()),
						CANTIDAD_DECIMALES);
			}
			if (entradaDto.getNumeroParto() <= 2) {
				mpGrowth = formatearDecimales(npg / effmpNpg, CANTIDAD_DECIMALES);
			} else {
				mpGrowth = 0.0;
			}
		} else {
			if (entradaDto.getGananciaPeso() == 0) {
				growthCalcium = 0.0;
				mpGrowth = 0.0;
			}
		}
		
		Double totalMpRequeriment = formatearDecimales(mpMaint + mpLact + mpPreg + mpGrowth, CANTIDAD_DECIMALES);

		Double caRequirement = formatearDecimales(
				fecalCalcium + urinaryCalcium + pregnancyCalcium + lactationCalcium + growthCalcium,
				CANTIDAD_DECIMALES);

		Double urinaryPhosphorous = formatearDecimales(0.002 * entradaDto.getPesoCorporal(), CANTIDAD_DECIMALES);

		Double growthPhosphorous = 0.0;
		if ("Jersey".equals(entradaDto.getRaza())) {
			growthPhosphorous = (1.2
					+ (4.635 * Math.pow(MW_JERSEY, 0.22)) * Math.pow(entradaDto.getPesoCorporal(), -0.22))
					* (entradaDto.getGananciaPeso());
		} else {
			growthPhosphorous = (1.2
					+ (4.635 * Math.pow(MW_HOLSTEIN, 0.22)) * Math.pow(entradaDto.getPesoCorporal(), -0.22))
					* (entradaDto.getGananciaPeso());
		}

		Double pRequirement = formatearDecimales(
				fecalPhosphorous + urinaryPhosphorous + pregnancyPhosphorous + lactationPhosphorous + growthPhosphorous,
				CANTIDAD_DECIMALES);

		Double mgFecal = formatearDecimales(0.003 * entradaDto.getPesoCorporal(), CANTIDAD_DECIMALES);

		Double mgGrowth = formatearDecimales(0.45 * entradaDto.getGananciaPeso(), CANTIDAD_DECIMALES);

		Double mgRequirement = formatearDecimales(mgFecal + 0.0 + mgPregnancy + mgLactation + mgGrowth,
				CANTIDAD_DECIMALES);

		Double kUrinary = formatearDecimales(0.038 * entradaDto.getPesoCorporal(), CANTIDAD_DECIMALES);

		Double kGrowth = formatearDecimales(1.6 * entradaDto.getGananciaPeso(), CANTIDAD_DECIMALES);

		Double kRequirement = formatearDecimales(kFecal + kUrinary + kPregnancy + kLactation + kGrowth,
				CANTIDAD_DECIMALES);

		Double totalConsumidoNel = formatearDecimales(cmsActual * nelAdjusted, CANTIDAD_DECIMALES);

		Double rupDigestible = 0.0;
		Double sumaProductoCpIntakeRupDigestible = 0.0;
		Double ca = 0.0, totalConsumidoCa = 0.0;
		Double p = 0.0, totalConsumidoP = 0.0;
		Double k = 0.0, totalConsumidoK = 0.0;
		Double mg = 0.0, totalConsumidoMg = 0.0;
		for (DietaDto dietaDto : dietaDtos) {
			Double productoCpIntakeRupDigestible = 0.0;
			Biblioteca biblioteca = bibliotecaRepository.findById(dietaDto.getIdBiblioteca()).get();
			if (biblioteca != null) {
				cpIntake = formatearDecimales(dietaDto.getCantidad() * biblioteca.getFdn() / 100 * 1000,
						CANTIDAD_DECIMALES);
				ca = formatearDecimales(((dietaDto.getCantidad()*biblioteca.getPorcentajeCa()/100)*1000)*biblioteca.getCoeficienteAbsorcionCa(), CANTIDAD_DECIMALES);
				p = formatearDecimales(((dietaDto.getCantidad()*biblioteca.getPorcentajeP()/100)*1000)*biblioteca.getCoeficienteAbsorcionP(), CANTIDAD_DECIMALES);
				k = formatearDecimales(((dietaDto.getCantidad()*biblioteca.getPorcentajeK()/100)*1000)*biblioteca.getCoeficienteAbsorcionK(), CANTIDAD_DECIMALES);
				mg = formatearDecimales(((dietaDto.getCantidad()*biblioteca.getPorcentajeMg())/1000)*biblioteca.getCoeficienteAbsorcionMg(), CANTIDAD_DECIMALES);
				if (entradaDto.getNumeroParto() == 0) {
					rupDigestible = 0.0;
				} else {
					if ("Forraje".equals(biblioteca.getTipo())) {
						rupDigestible = formatearDecimales((biblioteca.getFraccionB()
								* (kpOfWetForage / (kpOfWetForage + biblioteca.getKdFraccionB()))
								+ biblioteca.getFraccionC()), CANTIDAD_DECIMALES);
					} else {
						rupDigestible = formatearDecimales(
								(biblioteca.getFraccionB()
										* (kpOfConcentrate / (kpOfConcentrate + biblioteca.getKdFraccionB()))
										+ biblioteca.getFraccionC()) * (biblioteca.getDigestibilidadPndr() / 100),
								CANTIDAD_DECIMALES);
					}
				}
				productoCpIntakeRupDigestible = formatearDecimales( cpIntake * rupDigestible, CANTIDAD_DECIMALES);
			}
			sumaProductoCpIntakeRupDigestible = formatearDecimales(sumaProductoCpIntakeRupDigestible + productoCpIntakeRupDigestible, CANTIDAD_DECIMALES);
			totalConsumidoCa = formatearDecimales(totalConsumidoCa + ca, CANTIDAD_DECIMALES);
			totalConsumidoP = formatearDecimales(totalConsumidoP + p, CANTIDAD_DECIMALES);
			totalConsumidoK = formatearDecimales(totalConsumidoK + k, CANTIDAD_DECIMALES);
			totalConsumidoMg = formatearDecimales(totalConsumidoMg + mg, CANTIDAD_DECIMALES);
		}

		Double totalConsumidoPm = formatearDecimales(mpBact + (sumaProductoCpIntakeRupDigestible/100)+(1.9*cmsActual*0.4*6.25), CANTIDAD_DECIMALES);
		
		Double balanceNel = formatearDecimales(totalConsumidoNel-totalNeRequirement, CANTIDAD_DECIMALES);
		Double balancePm = formatearDecimales(totalConsumidoPm-totalMpRequeriment, CANTIDAD_DECIMALES);
		Double balanceCa = formatearDecimales(totalConsumidoCa-caRequirement, CANTIDAD_DECIMALES);
		Double balanceP = formatearDecimales(totalConsumidoP-pRequirement, CANTIDAD_DECIMALES);
		Double balanceK = formatearDecimales(totalConsumidoK-kRequirement, CANTIDAD_DECIMALES);
		Double balanceMg = formatearDecimales(totalConsumidoMg-mgRequirement, CANTIDAD_DECIMALES);
		
		Double porcentajeNel = formatearDecimales((balanceNel/totalNeRequirement)*100, CANTIDAD_DECIMALES);
		Double porcentajePm = formatearDecimales((balancePm/totalMpRequeriment)*100, CANTIDAD_DECIMALES);
		Double porcentajeCa = formatearDecimales((balanceCa/caRequirement)*100, CANTIDAD_DECIMALES);
		Double porcentajeP = formatearDecimales((balanceP/pRequirement)*100, CANTIDAD_DECIMALES);
		Double porcentajeK = formatearDecimales((balanceK/kRequirement)*100, CANTIDAD_DECIMALES);
		Double porcentajeMg = formatearDecimales((balanceMg/mgRequirement)*100, CANTIDAD_DECIMALES);
		
		modeloDto.setActualDMI(cmsActual);
		modeloDto.setTotalDMFeed(totalDmFeed);
		modeloDto.setScurfRequirement(scurfRequirement);
		modeloDto.setUrinaryRequirement(urinaryRequirement);
		modeloDto.setMetabolicFecalProteinReq(metabolicFecalProteinReq);
		modeloDto.setMpEndougenosRequirement(mpEndoungeosRequirement);
		modeloDto.setMpMaint(mpMaint);
		modeloDto.setMpLact(mpLact);
		modeloDto.setMpPreg(mpPreg);
		modeloDto.setMpGrowth(mpGrowth);
		modeloDto.setTotalMpRequirement(totalMpRequeriment);
		modeloDto.setNeMaint(neMaintEnergy);
		modeloDto.setyEn(yEn);
		modeloDto.setNePreg(nePreg);
		modeloDto.setGrowht(growht);
		modeloDto.setTotalNeRequirement(totalNeRequirement);
		modeloDto.setFecalCalcium(fecalCalcium);
		modeloDto.setUrinaryCalcium(urinaryCalcium);
		modeloDto.setPregnancyCalcium(pregnancyCalcium);
		modeloDto.setLactationCalcium(lactationCalcium);
		modeloDto.setGrowthCalcium(growthCalcium);
		modeloDto.setFecalPhosphorous(fecalPhosphorous);
		modeloDto.setUrinaryPhosphorous(urinaryPhosphorous);
		modeloDto.setPregnancyPhosphorous(pregnancyPhosphorous);
		modeloDto.setLactationPhosphorous(lactationPhosphorous);
		modeloDto.setGrowthPhosphorous(growthPhosphorous);
		modeloDto.setCaRequirement(caRequirement);
		modeloDto.setpRequirement(pRequirement);
		modeloDto.setMgFecal(mgFecal);
		modeloDto.setMgUrinary(0.0);
		modeloDto.setMgPregnancy(mgPregnancy);
		modeloDto.setMgLactation(mgLactation);
		modeloDto.setMgGrowth(mgGrowth);
		modeloDto.setMgRequirement(mgRequirement);
		modeloDto.setkFecal(kFecal);
		modeloDto.setkUrinary(kUrinary);
		modeloDto.setkPregnancy(kPregnancy);
		modeloDto.setkLactation(kLactation);
		modeloDto.setkGrowth(kGrowth);
		modeloDto.setkRequirement(kRequirement);
		modeloDto.setTotalConsumidoNel(totalConsumidoNel);
		modeloDto.setTotalConsumidoPm(totalConsumidoPm);
		modeloDto.setTotalConsumidoCa(totalConsumidoCa);
		modeloDto.setTotalConsumidoP(totalConsumidoP);
		modeloDto.setTotalConsumidoK(totalConsumidoK);
		modeloDto.setTotalConsumidoMg(totalConsumidoMg);
		modeloDto.setBalanceNel(balanceNel);
		modeloDto.setBalancePm(balancePm);
		modeloDto.setBalanceCa(balanceCa);
		modeloDto.setBalanceP(balanceP);
		modeloDto.setBalanceK(balanceK);
		modeloDto.setBalanceMg(balanceMg);
		modeloDto.setPorcentajeCa(porcentajeCa);
		modeloDto.setPorcentajeK(porcentajeK);
		modeloDto.setPorcentajeMg(porcentajeMg);
		modeloDto.setPorcentajeNel(porcentajeNel);
		modeloDto.setPorcentajeP(porcentajeP);
		modeloDto.setPorcentajePm(porcentajePm);
		return modeloDto;
	}

	private static Double formatearDecimales(Double numero, Integer numeroDecimales) {
		return Math.round(numero * Math.pow(10, numeroDecimales)) / Math.pow(10, numeroDecimales);
	}

}
