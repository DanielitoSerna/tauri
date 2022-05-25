package co.com.udea.tauri.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.com.udea.tauri.dtos.BalanceDto;
import co.com.udea.tauri.dtos.ConsumoMateriaSecaDto;
import co.com.udea.tauri.dtos.DietaDto;
import co.com.udea.tauri.dtos.EmisionGeiDto;
import co.com.udea.tauri.dtos.EntradaDto;
import co.com.udea.tauri.dtos.ModeloDto;
import co.com.udea.tauri.dtos.RelacionBeneficioCostoDto;
import co.com.udea.tauri.entities.Biblioteca;
import co.com.udea.tauri.repositories.BibliotecaRepository;
import co.com.udea.tauri.services.IModeloService;

@Service
public class ModeloService implements IModeloService {

	private static final Double MW_JERSEY = 454.0, MW_HOLSTEIN = 680.0, CBW_CONSTANT = 0.06275;
	private static final Double WOL_CONSTANT = 7.0, MILK_PROD_CONSTANT = 1.03, MILK_TRUE_PROTEIN_CONSTANT = 0.93;
	private static final Double FCM_CONSTANT_DOUBLE = 0.4;
	private static final Integer CANTIDAD_DECIMALES = 2, FCM_CONSTANT_INTEGER = 15, CONSTANT_CIEN = 100;
	private static final Double KP_OF_WET_FORAGE_CONSTANT = 3.054, KP_OF_WET_FORAGE_CONSTANT_DOS = 0.614;
	private static final Double KP_OF_WET_CONCENTRATE_CONSTANT = 2.904, KP_OF_WET_CONCENTRATE_CONSTANT_DOS = 1.375;
	private static final Double KP_OF_WET_CONCENTRATE_CONSTANT_TRES = 0.02;
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
		Double milkProd = formatearDecimales(entradaDto.getProduccionLeche() * MILK_PROD_CONSTANT, CANTIDAD_DECIMALES);
		Double milkTrueProtein = formatearDecimales(entradaDto.getProteinaCruda() * MILK_TRUE_PROTEIN_CONSTANT,
				CANTIDAD_DECIMALES);
		Double fcm = formatearDecimales(
				(FCM_CONSTANT_DOUBLE * milkProd
						+ FCM_CONSTANT_INTEGER * (milkProd * entradaDto.getGrasa() / CONSTANT_CIEN)),
				CANTIDAD_DECIMALES);
		Double yprotn = formatearDecimales((milkProd * (milkTrueProtein / CONSTANT_CIEN)), 2);
		Double cbw = 0.0;
		if ("Jersey".equals(entradaDto.getRaza())) {
			cbw = MW_JERSEY * CBW_CONSTANT;
		} else {
			cbw = MW_HOLSTEIN * CBW_CONSTANT;
		}
		Double cmsActual = 0.0;
		Double cmsConcentrate = 0.0;
		Double tdn = 0.0;
		Double sumaTdn = 0.0;
		Double cpIntake = 0.0;
		for (DietaDto dietaDto : dietaDtos) {
			cmsActual = cmsActual + dietaDto.getCantidad();
			Biblioteca biblioteca = bibliotecaRepository.findById(dietaDto.getIdBiblioteca()).get();
			if (biblioteca != null) {
				Double ed = biblioteca.getEd() != null ? biblioteca.getEd() : 0.0;
				tdn = formatearDecimales((dietaDto.getCantidad() * (ed / 0.04409)) / CONSTANT_CIEN, CANTIDAD_DECIMALES);

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
		Double kpOfConcentrate = formatearDecimales(
				KP_OF_WET_CONCENTRATE_CONSTANT
						+ KP_OF_WET_CONCENTRATE_CONSTANT_DOS
								* (cmsActual / entradaDto.getPesoCorporal() * CONSTANT_CIEN)
						- KP_OF_WET_CONCENTRATE_CONSTANT_TRES * (cmsConcentrate / cmsActual * CONSTANT_CIEN),
				CANTIDAD_DECIMALES);
		Double cw = formatearDecimales((18 + ((entradaDto.getDiasPrenez() - 190) * 0.665)) * (cbw / 45),
				CANTIDAD_DECIMALES);
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
				Double fdn = biblioteca.getFdn() != null ? biblioteca.getFdn() : 0.0;
				Double fraccionB = biblioteca.getFraccionB() != null ? biblioteca.getFraccionB() : 0.0;
				Double kdFraccionB = biblioteca.getKdFraccionB() != null ? biblioteca.getKdFraccionB() : 0.0;
				Double fraccionA = biblioteca.getFraccionA() != null ? biblioteca.getFraccionA() : 0.0;

				cpIntake = formatearDecimales(dietaDto.getCantidad() * fdn / 100 * 1000, CANTIDAD_DECIMALES);

				if (new Double(0).equals(dietaDto.getCantidad())) {
					porcentajeRdp = 0.0;
				}
				if ("Forraje".equals(biblioteca.getTipo())) {
					porcentajeRdp = formatearDecimales(
							fraccionA + (fraccionB * (kdFraccionB / (kdFraccionB + kpOfWetForage))),
							CANTIDAD_DECIMALES);
				} else {
					if ("Concentrado".equals(biblioteca.getTipo())) {
						porcentajeRdp = formatearDecimales(
								fraccionA + (fraccionB * (kdFraccionB / (kdFraccionB + kpOfConcentrate))),
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

		Double milkEneg = 0.0;
		if (entradaDto.getLactosa() == 0) {
			milkEneg = formatearDecimales(
					(0.0929 * entradaDto.getGrasa()) + (0.0547 * (milkTrueProtein / 0.93)) + 0.192, CANTIDAD_DECIMALES);
		} else {
			if (entradaDto.getLactosa() > 0) {
				milkEneg = formatearDecimales((0.0929 * entradaDto.getGrasa()) + (0.0547 * (milkTrueProtein / 0.93))
						+ (0.0395 * entradaDto.getLactosa()), CANTIDAD_DECIMALES);
			}
		}

		Double yEn = formatearDecimales(milkEneg * milkProd, CANTIDAD_DECIMALES);

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
			lactationPhosphorous = formatearDecimales(0.9 * milkProd, CANTIDAD_DECIMALES);
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
				Double fdn = biblioteca.getFdn() != null ? biblioteca.getFdn() : 0.0;
				Double porcentajeCa = biblioteca.getPorcentajeCa() != null ? biblioteca.getPorcentajeCa() : 0.0;
				Double coeficienteAbsorcionCa = biblioteca.getCoeficienteAbsorcionCa() != null
						? biblioteca.getCoeficienteAbsorcionCa()
						: 0.0;
				Double porcentajeP = biblioteca.getPorcentajeP() != null ? biblioteca.getPorcentajeP() : 0.0;
				Double coeficienteAbsorcionP = biblioteca.getCoeficienteAbsorcionP() != null
						? biblioteca.getCoeficienteAbsorcionP()
						: 0.0;
				Double porcentajeK = biblioteca.getPorcentajeK() != null ? biblioteca.getPorcentajeK() : 0.0;
				Double coeficienteAbsorcionK = biblioteca.getCoeficienteAbsorcionK() != null
						? biblioteca.getCoeficienteAbsorcionK()
						: 0.0;
				Double porcentajeMg = biblioteca.getPorcentajeMg() != null ? biblioteca.getPorcentajeMg() : 0.0;
				Double coeficienteAbsorcionMg = biblioteca.getCoeficienteAbsorcionMg() != null
						? biblioteca.getCoeficienteAbsorcionMg()
						: 0.0;

				cpIntake = formatearDecimales(dietaDto.getCantidad() * fdn / 100 * 1000, CANTIDAD_DECIMALES);
				ca = formatearDecimales(((dietaDto.getCantidad() * porcentajeCa / 100) * 1000) * coeficienteAbsorcionCa,
						CANTIDAD_DECIMALES);
				p = formatearDecimales(((dietaDto.getCantidad() * porcentajeP / 100) * 1000) * coeficienteAbsorcionP,
						CANTIDAD_DECIMALES);
				k = formatearDecimales(((dietaDto.getCantidad() * porcentajeK / 100) * 1000) * coeficienteAbsorcionK,
						CANTIDAD_DECIMALES);
				mg = formatearDecimales(((dietaDto.getCantidad() * porcentajeMg) / 1000) * coeficienteAbsorcionMg,
						CANTIDAD_DECIMALES);
				if (entradaDto.getNumeroParto() == 0) {
					rupDigestible = 0.0;
				} else {
					Double fraccionB = biblioteca.getFraccionB() != null ? biblioteca.getFraccionB() : 0.0;
					Double fraccionC = biblioteca.getFraccionC() != null ? biblioteca.getFraccionC() : 0.0;
					Double kdFraccionB = biblioteca.getKdFraccionB() != null ? biblioteca.getKdFraccionB() : 0.0;
					Double digestibilidadPndr = biblioteca.getDigestibilidadPndr() != null
							? biblioteca.getDigestibilidadPndr()
							: 0.0;
					if ("Forraje".equals(biblioteca.getTipo())) {
						rupDigestible = formatearDecimales(
								(fraccionB * (kpOfWetForage / (kpOfWetForage + kdFraccionB)) + fraccionC),
								CANTIDAD_DECIMALES);
					} else {
						rupDigestible = formatearDecimales(
								(fraccionB * (kpOfConcentrate / (kpOfConcentrate + kdFraccionB)) + fraccionC)
										* (digestibilidadPndr / 100),
								CANTIDAD_DECIMALES);
					}
				}
				productoCpIntakeRupDigestible = formatearDecimales(cpIntake * rupDigestible, CANTIDAD_DECIMALES);
			}
			sumaProductoCpIntakeRupDigestible = formatearDecimales(
					sumaProductoCpIntakeRupDigestible + productoCpIntakeRupDigestible, CANTIDAD_DECIMALES);
			totalConsumidoCa = formatearDecimales(totalConsumidoCa + ca, CANTIDAD_DECIMALES);
			totalConsumidoP = formatearDecimales(totalConsumidoP + p, CANTIDAD_DECIMALES);
			totalConsumidoK = formatearDecimales(totalConsumidoK + k, CANTIDAD_DECIMALES);
			totalConsumidoMg = formatearDecimales(totalConsumidoMg + mg, CANTIDAD_DECIMALES);
		}

		Double totalConsumidoPm = formatearDecimales(
				mpBact + (sumaProductoCpIntakeRupDigestible / 100) + (1.9 * cmsActual * 0.4 * 6.25),
				CANTIDAD_DECIMALES);

		Double balanceNel = formatearDecimales(totalConsumidoNel - totalNeRequirement, CANTIDAD_DECIMALES);
		Double balancePm = formatearDecimales(totalConsumidoPm - totalMpRequeriment, CANTIDAD_DECIMALES);
		Double balanceCa = formatearDecimales(totalConsumidoCa - caRequirement, CANTIDAD_DECIMALES);
		Double balanceP = formatearDecimales(totalConsumidoP - pRequirement, CANTIDAD_DECIMALES);
		Double balanceK = formatearDecimales(totalConsumidoK - kRequirement, CANTIDAD_DECIMALES);
		Double balanceMg = formatearDecimales(totalConsumidoMg - mgRequirement, CANTIDAD_DECIMALES);

		Double porcentajeNel = formatearDecimales((balanceNel / totalNeRequirement) * 100, CANTIDAD_DECIMALES);
		Double porcentajePm = formatearDecimales((balancePm / totalMpRequeriment) * 100, CANTIDAD_DECIMALES);
		Double porcentajeCa = formatearDecimales((balanceCa / caRequirement) * 100, CANTIDAD_DECIMALES);
		Double porcentajeP = formatearDecimales((balanceP / pRequirement) * 100, CANTIDAD_DECIMALES);
		Double porcentajeK = formatearDecimales((balanceK / kRequirement) * 100, CANTIDAD_DECIMALES);
		Double porcentajeMg = formatearDecimales((balanceMg / mgRequirement) * 100, CANTIDAD_DECIMALES);

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

	@Override
	public RelacionBeneficioCostoDto calcularRelacionBeneficio(EntradaDto entradaDto, List<DietaDto> dietaDtos) {
		RelacionBeneficioCostoDto relacionBeneficioCostoDto = new RelacionBeneficioCostoDto();
		Double cmsActual = 0.0;
		Double sumaPrecioDieta = 0.0;
		for (DietaDto dietaDto : dietaDtos) {
			cmsActual = cmsActual + dietaDto.getCantidad();
			Double precioUnidadDieta = dietaDto.getCantidad() * dietaDto.getCantidadOfrecido();
			sumaPrecioDieta = sumaPrecioDieta + precioUnidadDieta;
		}
		Double eficienciaAlimentacia = formatearDecimales(entradaDto.getProduccionLeche() / cmsActual,
				CANTIDAD_DECIMALES);
		Double precioDieta = formatearDecimales(sumaPrecioDieta / cmsActual, CANTIDAD_DECIMALES);
		Double costoLitroLeche = formatearDecimales((precioDieta * (1 / eficienciaAlimentacia)), CANTIDAD_DECIMALES);
		Double margenUtilidadBruta = formatearDecimales(entradaDto.getPrecioVenta() - costoLitroLeche,
				CANTIDAD_DECIMALES);
		Double margenPorcentual = formatearDecimales((margenUtilidadBruta / entradaDto.getPrecioVenta()) * 100,
				CANTIDAD_DECIMALES);
		Double relacionPrecio = formatearDecimales(entradaDto.getPrecioVenta() / precioDieta, CANTIDAD_DECIMALES);

		relacionBeneficioCostoDto.setEficienciaAlimentica(eficienciaAlimentacia);
		relacionBeneficioCostoDto.setCostoDieta(precioDieta);
		relacionBeneficioCostoDto.setCostoLitroLeche(costoLitroLeche);
		relacionBeneficioCostoDto.setMargenUtilidadBruta(margenUtilidadBruta);
		relacionBeneficioCostoDto.setMargenPorcentual(margenPorcentual);
		relacionBeneficioCostoDto.setRelacionPrecioVentaCostoAlimentacion(relacionPrecio);
		return relacionBeneficioCostoDto;
	}

	@Override
	public EmisionGeiDto calcularEmisionGei(EntradaDto entradaDto, List<DietaDto> dietaDtos) {
		EmisionGeiDto emisionGeiDto = new EmisionGeiDto();

		Double cmsActual = 0.0, ge = 0.0, cnf = 0.0, geProducto = 0.0, sumeGeProducto = 0.0;
		for (DietaDto dietaDto : dietaDtos) {
			cmsActual = cmsActual + dietaDto.getCantidad();
			Biblioteca biblioteca = bibliotecaRepository.findById(dietaDto.getIdBiblioteca()).get();

			if (biblioteca != null) {
				Double pb = biblioteca.getPb() != null ? biblioteca.getPb() : 0.0;
				Double fdn = biblioteca.getFdn() != null ? biblioteca.getFdn() : 0.0;
				Double grasaCruda = biblioteca.getGrasaCruda() != null ? biblioteca.getGrasaCruda() : 0.0;
				Double ceniza = biblioteca.getCeniza() != null ? biblioteca.getCeniza() : 0.0;
				if (dietaDto.getCantidad() == 0) {
					cnf = 0.0;
				} else {
					if (dietaDto.getCantidad() > 0) {
						cnf = formatearDecimales((100 - (pb + fdn + grasaCruda + ceniza)), CANTIDAD_DECIMALES);
					}
				}
				ge = formatearDecimales(((pb * 5.65) + (grasaCruda * 9.4) + (fdn * 4.25) + (cnf * 4.15)) / 100,
						CANTIDAD_DECIMALES);
			}
			geProducto = dietaDto.getCantidad() * ge;
			sumeGeProducto = sumeGeProducto + geProducto;
		}
		Double milkProd = formatearDecimales(entradaDto.getProduccionLeche() * MILK_PROD_CONSTANT, CANTIDAD_DECIMALES);
		Double fcm = formatearDecimales(
				(FCM_CONSTANT_DOUBLE * milkProd
						+ FCM_CONSTANT_INTEGER * (milkProd * entradaDto.getGrasa() / CONSTANT_CIEN)),
				CANTIDAD_DECIMALES);

		Double metanoDiaLitro = formatearDecimales(21.1284 * cmsActual, CANTIDAD_DECIMALES);
		Double metanoDiaGramo = formatearDecimales(metanoDiaLitro * 0.717, CANTIDAD_DECIMALES);
		Double metanoMsConsumidaLitro = formatearDecimales(metanoDiaLitro / cmsActual, CANTIDAD_DECIMALES);
		Double metanoMsConsumidaGramo = formatearDecimales(metanoDiaGramo / cmsActual, CANTIDAD_DECIMALES);
		Double metanoLcgLitro = formatearDecimales(metanoDiaLitro / fcm, CANTIDAD_DECIMALES);
		Double metanoLcgGramo = formatearDecimales(metanoDiaGramo / fcm, CANTIDAD_DECIMALES);
		Double metanoFactorEmision = formatearDecimales((metanoDiaGramo * 365) / 1000, CANTIDAD_DECIMALES);
		Double metanoYm = formatearDecimales(((metanoDiaLitro * 0.00945) / sumeGeProducto) * 100, CANTIDAD_DECIMALES);

		Double dioxidoCarbonoDiaGramo = formatearDecimales(24.084 * metanoDiaGramo + 3280.5, CANTIDAD_DECIMALES);
		Double dioxidoCarbonoDiaLitro = formatearDecimales(dioxidoCarbonoDiaGramo / 1.976, CANTIDAD_DECIMALES);
		Double dioxidoCarbonoMsConsumidoGramo = formatearDecimales(dioxidoCarbonoDiaGramo / cmsActual,
				CANTIDAD_DECIMALES);
		Double dioxidoCarbonoMsConsumidoLitro = formatearDecimales(dioxidoCarbonoDiaLitro / cmsActual,
				CANTIDAD_DECIMALES);
		Double dioxidoCarbonoLgcGramo = formatearDecimales(dioxidoCarbonoDiaGramo / fcm, CANTIDAD_DECIMALES);
		Double dioxidoCarbonoLcgLitro = formatearDecimales(dioxidoCarbonoDiaLitro / fcm, CANTIDAD_DECIMALES);
		Double dioxidoCarbonoFactorEmision = formatearDecimales((dioxidoCarbonoDiaGramo * 365) / 1000,
				CANTIDAD_DECIMALES);

		Double dioxidoCarbonoEqDiaGramo = formatearDecimales((metanoDiaGramo * 28) + dioxidoCarbonoDiaGramo,
				CANTIDAD_DECIMALES);
		Double dioxidoCarbonoEqMsConsumidoGramo = formatearDecimales(dioxidoCarbonoEqDiaGramo / cmsActual,
				CANTIDAD_DECIMALES);
		Double dioxidoCarbonoEqLcgGramo = formatearDecimales(dioxidoCarbonoEqDiaGramo / fcm, CANTIDAD_DECIMALES);
		Double dioxidoCarbonoEqFactorEmision = formatearDecimales((dioxidoCarbonoEqDiaGramo * 365) / 1000,
				CANTIDAD_DECIMALES);

		Double msAbsorbidaProduccionFecal = formatearDecimales(cmsActual * (69 / 100), CANTIDAD_DECIMALES);
		Double msFecalProduccionFecal = formatearDecimales(cmsActual - msAbsorbidaProduccionFecal, CANTIDAD_DECIMALES);
		Double emisionMetanoDiaProduccionFecal = formatearDecimales(msFecalProduccionFecal * 1.4, CANTIDAD_DECIMALES);
		Double emisionMetanoAnioProduccionFecal = formatearDecimales((emisionMetanoDiaProduccionFecal * 365) / 1000,
				CANTIDAD_DECIMALES);

		Double emisionMetanoDiaProduccionUrinaria = formatearDecimales(27.0 * 0, CANTIDAD_DECIMALES);
		Double emisionMetanoAnioProduccionUrinaria = formatearDecimales(
				(emisionMetanoDiaProduccionUrinaria * 365) / 1000, CANTIDAD_DECIMALES);

		Double metanoExcretaFactorEmision = formatearDecimales(
				emisionMetanoAnioProduccionFecal + emisionMetanoAnioProduccionUrinaria, CANTIDAD_DECIMALES);
		Double metanoDiaExcretaGramo = formatearDecimales((metanoExcretaFactorEmision * 1000) / 365,
				CANTIDAD_DECIMALES);
		Double metanoMsConsumidoExcretaGramo = formatearDecimales(metanoDiaExcretaGramo / cmsActual,
				CANTIDAD_DECIMALES);
		Double metanoLcgExcretaGramo = formatearDecimales(metanoDiaExcretaGramo / fcm, CANTIDAD_DECIMALES);

		Double msAbsorbidaCo2Fecal = formatearDecimales(cmsActual * (69 / 100), CANTIDAD_DECIMALES);
		Double msFecalCo2Fecal = formatearDecimales(cmsActual - msAbsorbidaCo2Fecal, CANTIDAD_DECIMALES);
		Double emisionCo2DiaFecal = formatearDecimales(msFecalCo2Fecal * 508.9, CANTIDAD_DECIMALES);
		Double emisionCo2anioFecal = formatearDecimales((emisionCo2DiaFecal * 365) / 1000, CANTIDAD_DECIMALES);

		Double emisionCo2diaUrinaria = formatearDecimales(27 * 30.2, CANTIDAD_DECIMALES);
		Double emisionCo2anioUrinaria = formatearDecimales((emisionCo2diaUrinaria * 365) / 1000, CANTIDAD_DECIMALES);

		Double dioxidoCarbonoExcretaFactorEmision = formatearDecimales(emisionCo2anioFecal + emisionCo2anioUrinaria,
				CANTIDAD_DECIMALES);
		Double dioxidoCarbonoExcretaDiaGramo = formatearDecimales((dioxidoCarbonoExcretaFactorEmision * 1000) / 365,
				CANTIDAD_DECIMALES);
		Double dioxidoCarbonoExcretaMsConsumidoGramo = formatearDecimales(dioxidoCarbonoExcretaDiaGramo / cmsActual,
				CANTIDAD_DECIMALES);
		Double dioxidoCarbonoExcretaLgcGramo = formatearDecimales(dioxidoCarbonoExcretaDiaGramo / fcm,
				CANTIDAD_DECIMALES);

		Double msAbsorbidaN2oFecal = formatearDecimales(cmsActual * (69 / 100), CANTIDAD_DECIMALES);
		Double msFecalN2oFecal = formatearDecimales(cmsActual - msAbsorbidaN2oFecal, CANTIDAD_DECIMALES);
		Double nAplicadoFecal = formatearDecimales(msFecalN2oFecal * 0.03, CANTIDAD_DECIMALES);
		Double emisionNN2oDiaFecal = formatearDecimales((nAplicadoFecal * (0.32 / 100)) * 1000, CANTIDAD_DECIMALES);
		Double emisionN2oDiaFecal = formatearDecimales(emisionNN2oDiaFecal / 0.6364, CANTIDAD_DECIMALES);
		Double emisionN2oAnioFecal = formatearDecimales((emisionN2oDiaFecal * 365) / 1000, CANTIDAD_DECIMALES);

		Double nAplicadoUrinario = formatearDecimales(27 * 0.006, CANTIDAD_DECIMALES);
		Double emisionNN2oUrinario = formatearDecimales((nAplicadoUrinario * (1.67 / 100)) * 1000, CANTIDAD_DECIMALES);
		Double emisionN2oDiaUrinario = formatearDecimales(emisionNN2oUrinario / 0.6364, CANTIDAD_DECIMALES);
		Double emisionN2oAnioUrinario = formatearDecimales((emisionN2oDiaUrinario * 365) / 1000, CANTIDAD_DECIMALES);

		Double oxidoNitrosoFactorEmision = formatearDecimales(emisionN2oAnioFecal + emisionN2oAnioUrinario,
				CANTIDAD_DECIMALES);
		Double oxidoNitrosoDiaGramo = formatearDecimales((oxidoNitrosoFactorEmision * 1000) / 365, CANTIDAD_DECIMALES);
		Double oxidoNitrosoMsConsumidoGramo = formatearDecimales(oxidoNitrosoDiaGramo / cmsActual, CANTIDAD_DECIMALES);
		Double oxidoNitrosoLcgGramo = formatearDecimales(oxidoNitrosoDiaGramo / fcm, CANTIDAD_DECIMALES);

		Double msAbsorbidaCo2EqFecal = formatearDecimales(cmsActual * (69 / 100), CANTIDAD_DECIMALES);
		Double msFecalCo2EqFecal = formatearDecimales(cmsActual - msAbsorbidaCo2EqFecal, CANTIDAD_DECIMALES);
		Double emisionCo2EqDiaFecal = formatearDecimales(msFecalCo2EqFecal * 564, CANTIDAD_DECIMALES);
		Double emisionCo2EqAnioFecal = formatearDecimales((emisionCo2EqDiaFecal * 365) / 1000, CANTIDAD_DECIMALES);

		Double emisionCo2EqDiaUrinario = formatearDecimales(17 * 74.98, CANTIDAD_DECIMALES);
		Double emisionCo2EqAnioUrinario = formatearDecimales((emisionCo2EqDiaUrinario * 365) / 1000,
				CANTIDAD_DECIMALES);

		Double co2FactorEmision = formatearDecimales(emisionCo2EqAnioFecal + emisionCo2EqAnioUrinario,
				CANTIDAD_DECIMALES);
		Double co2EqDiaGramo = formatearDecimales((co2FactorEmision * 1000) / 365, CANTIDAD_DECIMALES);
		Double co2EqMsConsumidaGramo = formatearDecimales(co2EqDiaGramo / cmsActual, CANTIDAD_DECIMALES);
		Double co2EqLgcGramo = formatearDecimales(co2EqDiaGramo / fcm, CANTIDAD_DECIMALES);

		// GASES DE ORIGEN ENTÉRICO
		emisionGeiDto.setMetanoDiaLitro(metanoDiaLitro);
		emisionGeiDto.setMetanoDiaGramo(metanoDiaGramo);
		emisionGeiDto.setMetanoMsConsumidaLitro(metanoMsConsumidaLitro);
		emisionGeiDto.setMetanoMsConsumidoGramo(metanoMsConsumidaGramo);
		emisionGeiDto.setMetanoLcgLitro(metanoLcgLitro);
		emisionGeiDto.setMetanoLcgGramo(metanoLcgGramo);
		emisionGeiDto.setMetanoFactorEmision(metanoFactorEmision);
		emisionGeiDto.setMetanoYm(metanoYm);
		emisionGeiDto.setDioxidoCarbonoDiaGramo(dioxidoCarbonoDiaGramo);
		emisionGeiDto.setDioxidoCarbonoDiaLitro(dioxidoCarbonoDiaLitro);
		emisionGeiDto.setDioxidoCarbonoMsConsumidoGramo(dioxidoCarbonoMsConsumidoGramo);
		emisionGeiDto.setDioxidoCarbonoMsConsumidoLitro(dioxidoCarbonoMsConsumidoLitro);
		emisionGeiDto.setDioxidoCarbonoLcgGramo(dioxidoCarbonoLgcGramo);
		emisionGeiDto.setDioxidoCarbonoLcgLitro(dioxidoCarbonoLcgLitro);
		emisionGeiDto.setDioxidoCarbonoFactorEmision(dioxidoCarbonoFactorEmision);
		emisionGeiDto.setDioxidoCarbonoEqDiaGramo(dioxidoCarbonoEqDiaGramo);
		emisionGeiDto.setDioxidoCarbonoEqMsConsumidoGramo(dioxidoCarbonoEqMsConsumidoGramo);
		emisionGeiDto.setDioxidoCarbonoEqLcgGramo(dioxidoCarbonoEqLcgGramo);
		emisionGeiDto.setDioxidoCarbonoEqFactorEmision(dioxidoCarbonoEqFactorEmision);

		// GASES DESDE LAS EXCRETAS EN EL SUELO
		emisionGeiDto.setMetanoExcretaFactorEmision(metanoExcretaFactorEmision);
		emisionGeiDto.setMetanoDiaExcretaGramo(metanoDiaExcretaGramo);
		emisionGeiDto.setMetanoMsConsumidoExcretaGramo(metanoMsConsumidoExcretaGramo);
		emisionGeiDto.setMetanoLcgExcretaGramo(metanoLcgExcretaGramo);
		emisionGeiDto.setDioxidoCarbonoExcretaFactorEmision(dioxidoCarbonoExcretaFactorEmision);
		emisionGeiDto.setDioxidoCarbonoExcretaDiaGramo(dioxidoCarbonoExcretaDiaGramo);
		emisionGeiDto.setDioxidoCarbonoExcretaMsConsumidoGramo(dioxidoCarbonoExcretaMsConsumidoGramo);
		emisionGeiDto.setDioxidoCarbonoExcretaLgcGramo(dioxidoCarbonoExcretaLgcGramo);
		emisionGeiDto.setOxidoNitrosoFactorEmision(oxidoNitrosoFactorEmision);
		emisionGeiDto.setOxidoNitrosoDiaGramo(oxidoNitrosoDiaGramo);
		emisionGeiDto.setOxidoNitrosoMsConsumidoGramo(oxidoNitrosoMsConsumidoGramo);
		emisionGeiDto.setOxidoNitrosoLcgGramo(oxidoNitrosoLcgGramo);
		emisionGeiDto.setCo2FactorEmision(co2FactorEmision);
		emisionGeiDto.setCo2EqDiaGramo(co2EqDiaGramo);
		emisionGeiDto.setCo2EqMsConsumidaGramo(co2EqMsConsumidaGramo);
		emisionGeiDto.setCo2EqLgcGramo(co2EqLgcGramo);
		emisionGeiDto.setEmisionMetanoAnioProduccionFecal(emisionMetanoAnioProduccionFecal);
		emisionGeiDto.setEmisionMetanoAnioProduccionUrinaria(emisionMetanoAnioProduccionUrinaria);
		emisionGeiDto.setEmisionCo2anioFecal(emisionCo2anioFecal);
		emisionGeiDto.setEmisionCo2anioUrinaria(emisionCo2anioUrinaria);
		emisionGeiDto.setEmisionN2oAnioFecal(emisionN2oAnioFecal);
		emisionGeiDto.setEmisionN2oAnioUrinario(emisionN2oAnioUrinario);
		emisionGeiDto.setEmisionCo2EqAnioFecal(emisionCo2EqAnioFecal);
		emisionGeiDto.setEmisionCo2EqAnioUrinario(emisionCo2EqAnioUrinario);
		return emisionGeiDto;
	}

	@Override
	public ConsumoMateriaSecaDto calcularConsumoMateriaSecaPredico(EntradaDto entradaDto) {
		ConsumoMateriaSecaDto consumoMateriaSecaDto = new ConsumoMateriaSecaDto();

		Double milkProd = formatearDecimales(entradaDto.getProduccionLeche() * MILK_PROD_CONSTANT, CANTIDAD_DECIMALES);
		Double fcm = formatearDecimales(
				(FCM_CONSTANT_DOUBLE * milkProd
						+ FCM_CONSTANT_INTEGER * (milkProd * entradaDto.getGrasa() / CONSTANT_CIEN)),
				CANTIDAD_DECIMALES);
		Double wol = formatearDecimales(entradaDto.getDiasLeche() / WOL_CONSTANT, CANTIDAD_DECIMALES);
		Double totalDmFeed = formatearDecimales(
				(CONSTANT_TOTAL_DM_FEED * fcm + CONSTANT_TOTAL_DM_FEED_DOS
						* Math.pow(entradaDto.getPesoCorporal(), CONSTANT_TOTAL_DM_FEED_TRES)
						* (1 - Math.exp(CONSTANT_TOTAL_DM_FEED_CUATRO * (wol + CONSTANT_TOTAL_DM_FEED_CINCO)))),
				CANTIDAD_DECIMALES);

		Double tauri = formatearDecimales(0.0906 * (Math.pow(entradaDto.getPesoCorporal(), 0.75)) + 0.3515 * fcm,
				CANTIDAD_DECIMALES);

		Double milkTrueProtein = formatearDecimales(entradaDto.getProteinaCruda() * MILK_TRUE_PROTEIN_CONSTANT,
				CANTIDAD_DECIMALES);
		Double milkEneg = 0.0;
		if (entradaDto.getLactosa() == 0) {
			milkEneg = formatearDecimales(
					(0.0929 * entradaDto.getGrasa()) + (0.0547 * (milkTrueProtein / 0.93)) + 0.192, CANTIDAD_DECIMALES);
		} else {
			if (entradaDto.getLactosa() > 0) {
				milkEneg = formatearDecimales((0.0929 * entradaDto.getGrasa()) + (0.0547 * (milkTrueProtein / 0.93))
						+ (0.0395 * entradaDto.getLactosa()), CANTIDAD_DECIMALES);
			}
		}

		Double yEn = formatearDecimales(milkEneg * milkProd, CANTIDAD_DECIMALES);

		Double parto = 0.0;
		if (entradaDto.getNumeroParto() == 1) {
			parto = 0.0;
		} else
			parto = 1.0;
		Double nrcEfectoAnimal = formatearDecimales(
				((3.7 + (parto * 5.7)) + (0.305 * yEn) + (0.022 * entradaDto.getPesoCorporal())
						+ (-0.689 + (parto * -1.87)) * entradaDto.getCondicionCorporal())
						* (1 - (0.212 + (parto * 0.136)) * Math.exp(-0.053 * entradaDto.getDiasLeche())),
				CANTIDAD_DECIMALES);

		consumoMateriaSecaDto.setNrc(totalDmFeed);
		consumoMateriaSecaDto.setTuari(tauri);
		consumoMateriaSecaDto.setNrcEfectosAnimales(nrcEfectoAnimal);
		return consumoMateriaSecaDto;
	}

	@Override
	public BalanceDto calcularBalance(EntradaDto entradaDto, List<DietaDto> dietaDtos) {
		BalanceDto balanceDto = new BalanceDto();

		Double cmsConcentrate = 0.0;
		Double cmsActual = 0.0;
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
		Double kpOfConcentrate = formatearDecimales(
				KP_OF_WET_CONCENTRATE_CONSTANT
						+ KP_OF_WET_CONCENTRATE_CONSTANT_DOS
								* (cmsActual / entradaDto.getPesoCorporal() * CONSTANT_CIEN)
						- KP_OF_WET_CONCENTRATE_CONSTANT_TRES * (cmsConcentrate / cmsActual * CONSTANT_CIEN),
				CANTIDAD_DECIMALES);

		cmsActual = 0.0;
		Double cmsForraje = 0.0;
		Double cmsConcentrado = 0.0;
		Double sumaFdn = 0.0;
		Double sumaFda = 0.0;
		Double sumaPb = 0.0;
		Double sumaAlmidon = 0.0;
		Double sumaPdr = 0.0;
		Double cpIntake = 0.0;
		Double rdp = 0.0;
		Double porcentajeRdp = 0.0;
		Double pndr = 0.0;
		Double sumaPndr = 0.0;
		for (DietaDto dietaDto : dietaDtos) {
			cmsActual = cmsActual + dietaDto.getCantidad();
			Biblioteca biblioteca = bibliotecaRepository.findById(dietaDto.getIdBiblioteca()).get();
			if (biblioteca != null) {
				Double fdn = biblioteca.getFdn() != null ? biblioteca.getFdn() : 0.0;
				Double fraccionB = biblioteca.getFraccionB() != null ? biblioteca.getFraccionB() : 0.0;
				Double kdFraccionB = biblioteca.getKdFraccionB() != null ? biblioteca.getKdFraccionB() : 0.0;
				Double fraccionA = biblioteca.getFraccionA() != null ? biblioteca.getFraccionA() : 0.0;
				Double fda = biblioteca.getFda() != null ? biblioteca.getFda() : 0.0;
				Double pbBiblioteca = biblioteca.getPb() != null ? biblioteca.getPb() : 0.0;
				Double almidon = biblioteca.getAlmidon() != null ? biblioteca.getAlmidon() : 0.0;
				if ("Forraje".equals(biblioteca.getTipo())) {
					cmsForraje = cmsForraje + dietaDto.getCantidad();
				} else {
					cmsConcentrado = cmsConcentrado + dietaDto.getCantidad();
				}
				sumaFdn = sumaFdn + fdn;
				sumaFda = sumaFda + fda;
				sumaPb = sumaPb + pbBiblioteca;
				sumaAlmidon = sumaAlmidon + almidon;
				cpIntake = formatearDecimales(dietaDto.getCantidad() * fdn / 100 * 1000, CANTIDAD_DECIMALES);
				if (new Double(0).equals(dietaDto.getCantidad())) {
					porcentajeRdp = 0.0;
				}
				if ("Forraje".equals(biblioteca.getTipo())) {
					porcentajeRdp = formatearDecimales(
							fraccionA + (fraccionB * (kdFraccionB / (kdFraccionB + kpOfWetForage))),
							CANTIDAD_DECIMALES);
				} else {
					if ("Concentrado".equals(biblioteca.getTipo())) {
						porcentajeRdp = formatearDecimales(
								fraccionA + (fraccionB * (kdFraccionB / (kdFraccionB + kpOfConcentrate))),
								CANTIDAD_DECIMALES);
					}
				}

				rdp = cpIntake * porcentajeRdp / 100;
				sumaPdr = sumaPdr + rdp;

				pndr = formatearDecimales(cpIntake * (100 - porcentajeRdp) / 100, CANTIDAD_DECIMALES);
				sumaPndr = sumaPndr + pndr;
			}
		}
		Double porcentajeForraje = formatearDecimales(cmsForraje / cmsActual * 100, CANTIDAD_DECIMALES);
		Double porcentajeConcentrado = formatearDecimales(cmsConcentrado / cmsActual * 100, CANTIDAD_DECIMALES);
		Double fdn = formatearDecimales(sumaFdn / cmsActual * 100, CANTIDAD_DECIMALES);
		Double fda = formatearDecimales(sumaFda / cmsActual * 100, CANTIDAD_DECIMALES);
		Double sumaPbDivido = formatearDecimales(sumaPb / 1000, CANTIDAD_DECIMALES);
		Double pb = formatearDecimales(sumaPbDivido / cmsActual * 100, CANTIDAD_DECIMALES);
		Double almidon = formatearDecimales(sumaAlmidon / cmsActual * 100, CANTIDAD_DECIMALES);
		Double sumaPdrDivido = formatearDecimales(sumaPdr / 1000, CANTIDAD_DECIMALES);
		Double pdr = formatearDecimales(((sumaPdrDivido / cmsActual * 100) / pb) * 100, CANTIDAD_DECIMALES);
		Double sumaPndrDivido = formatearDecimales(sumaPndr / 1000, CANTIDAD_DECIMALES);
		Double pndrBalance = formatearDecimales(((sumaPndrDivido / cmsActual * 100) / pb) * 100, CANTIDAD_DECIMALES);

		balanceDto.setPorcentajeForraje(porcentajeForraje);
		balanceDto.setPorcentajeConcentrado(porcentajeConcentrado);
		balanceDto.setFdn(fdn);
		balanceDto.setFda(fda);
		balanceDto.setPb(pb);
		balanceDto.setAlmidon(almidon);
		balanceDto.setPdr(pdr);
		balanceDto.setPndr(pndrBalance);
		return balanceDto;
	}

	private static Double formatearDecimales(Double numero, Integer numeroDecimales) {
		return Math.round(numero * Math.pow(10, numeroDecimales)) / Math.pow(10, numeroDecimales);
	}

}
