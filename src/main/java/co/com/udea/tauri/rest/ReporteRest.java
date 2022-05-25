package co.com.udea.tauri.rest;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.html.WebColors;
import com.lowagie.text.pdf.PdfCell;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import co.com.udea.tauri.dtos.DietaDto;
import co.com.udea.tauri.dtos.EmisionGeiDto;
import co.com.udea.tauri.dtos.EntradaDto;
import co.com.udea.tauri.dtos.ModeloDto;
import co.com.udea.tauri.dtos.RelacionBeneficioCostoDto;
import co.com.udea.tauri.services.impl.DietaService;
import co.com.udea.tauri.services.impl.EntradaService;
import co.com.udea.tauri.services.impl.ModeloService;

@RestController
@RequestMapping("api")
@CrossOrigin(origins = "*")
public class ReporteRest {

	private static final String CO2_KG_LCG4_0 = "CO2/kg LCG4.0%";
	private static final String CO2_KG_MS_CONSUMIDA = "CO2/kg MS consumida";
	private static final String CO2_DIA = "CO2/día";
	private static final String FACTOR_DE_EMISIÓN_KG_ANIMAL = "Factor de emisión (kg/animal/año)";
	private static final String CH4_KG_LCG4_0 = "CH4/kg LCG4.0%";
	private static final String CH4_KG_MS_CONSUMIDA = "CH4/kg MS consumida";
	private static final String CH4_DIA = "CH4/día";
	private static final String COLOR_SUBTITULO = "#ced4da";
	private static final String FORMAT_NUMBER = "#,##0.00";
	private static final int ALIGN_CENTER = Paragraph.ALIGN_CENTER;
	private static final String G_DIA = "(g/día)";
	
	@Autowired
	private DietaService dietaService;
	
	@Autowired
	private ModeloService modeloService;
	
	@Autowired
	private EntradaService entradaService;
	
	private EntradaDto entradaDto;
	private List<DietaDto> dieta;

	@GetMapping(path = "/exportar", produces = MediaType.APPLICATION_JSON_VALUE)
	public void listarBiblioteca(HttpServletResponse response, @RequestParam Integer reporteId)
			throws DocumentException, IOException {
		response.setContentType("application/pdf");
		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=Reporte " + reporteId + ".pdf";
		response.setHeader(headerKey, headerValue);
		export(response, reporteId);
	}

	private void crearDietaHeader(PdfPTable table) {
		table.addCell(crearCelda("Alimento", "N", false, true, false));
		table.addCell(crearCelda("Tipo", "N", false, true, false));
		table.addCell(crearCelda("Cantidad (kg MS/día)", "N", false, true, false));
		table.addCell(crearCelda("Cantidad como ofrecido (kg/día)", "N", false, true, false));
		table.addCell(crearCelda("Precio ($/kg MS alimento)", "N", false, true, false));
		table.addCell(crearCelda("Precio ($/kg alimento, como ofrecido)", "N", false, true, false));
	}

	private void crearBalanceHeader(PdfPTable table) {
		
		table.addCell(crearCelda("", "N", false, true, false));
		table.addCell(crearCelda("NEL", "N", false, true, false));
		table.addCell(crearCelda("PM", "N", false, true, false));
		table.addCell(crearCelda("Ca", "N", false, true, false));
		table.addCell(crearCelda("P", "N", false, true, false));
		table.addCell(crearCelda("K", "N", false, true, false));
		table.addCell(crearCelda("Mg", "N", false, true, false));
		
		table.addCell(crearCelda("", "N", false, true, false));
		table.addCell(crearCelda("(Mcal/día)", "N", false, true, false));
		table.addCell(crearCelda(G_DIA, "N", false, true, false));
		table.addCell(crearCelda(G_DIA, "N", false, true, false));
		table.addCell(crearCelda(G_DIA, "N", false, true, false));
		table.addCell(crearCelda(G_DIA, "N", false, true, false));
		table.addCell(crearCelda(G_DIA, "N", false, true, false));
	}
	
	private void crearEmisionHeader(PdfPTable table, String label, boolean litros) {
		if(litros) {
			table.addCell(crearCelda(label, "N", false, true, false));
			table.addCell(crearCelda("Gramos", "N", false, true, false));
			table.addCell(crearCelda("Litros", "N", false, true, false));
		} else {
			PdfPCell cell = crearCelda(label, "N", false, true, false);
			cell.setColspan(2);
			table.addCell(cell);
			table.addCell(crearCelda("Gramos", "N", false, true, false));
		}
	}
	
	private void crearEmisionValue(PdfPTable table, String label, String gramos, String litros, boolean fila) {
		if(gramos != null) {
			table.addCell(crearCelda(label, "N", false, false, fila));
			table.addCell(crearCelda(gramos, "N", true, false, fila));
		} else {
			PdfPCell cell = crearCelda(label, "N", false, false, fila);
			cell.setColspan(2);
			table.addCell(cell);
		}
		table.addCell(crearCelda(litros, "N", true, false, fila));
	}

	private void agregarAlimentos(PdfPTable table, Integer idReporte) {
		dieta = dietaService.listarDieta(idReporte);

		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
		decimalFormatSymbols.setDecimalSeparator(',');
		decimalFormatSymbols.setGroupingSeparator('.');
		DecimalFormat format = new DecimalFormat(FORMAT_NUMBER, decimalFormatSymbols);
		
		int contador = 0;
		double sumaCantidad = 0;
		double sumaForraje = 0;
		double sumaConcentrado = 0;
		double sumaPrecio = 0;
		for (DietaDto dietaDto : dieta) {
			table.addCell(crearCelda(dietaDto.getBiblioteca().getNombre(), "N", false, false, contador % 2 == 0));
			table.addCell(crearCelda(dietaDto.getBiblioteca().getTipo(), "N", false, false, contador % 2 == 0));
			table.addCell(crearCelda(format.format(dietaDto.getCantidad()), "N", true, false, contador % 2 == 0));
			table.addCell(crearCelda(format.format(dietaDto.getCantidadOfrecido()), "N", true, false, contador % 2 == 0));
			table.addCell(crearCelda("$ " + format.format(dietaDto.getPrecio()), "N", true, false, contador % 2 == 0));
			
			if(dietaDto.getBiblioteca().getMs() != null) {
				double precio = dietaDto.getPrecio() * (dietaDto.getBiblioteca().getMs() / 100);
				table.addCell(crearCelda("$ " + format.format(precio), "N", true, false, contador % 2 == 0));
			} else {
				table.addCell(crearCelda("$ 0,00", "N", true, false, contador % 2 == 0));
			}
			
			contador += 1;
			sumaCantidad += dietaDto.getCantidad();
			sumaPrecio += (dietaDto.getPrecio() * dietaDto.getCantidad());
			if("Forraje".equals(dietaDto.getBiblioteca().getTipo())) {
				sumaForraje += dietaDto.getCantidad();
			} else {
				sumaConcentrado += dietaDto.getCantidad();
			}
		}
		
		PdfPCell cell = crearCelda(" ", "", false, true, false);
		cell.setColspan(6);
		table.addCell(cell);
		
		cell = crearCelda("Consumo de materia seca actual (CMSact)", "", false, true, false);
		cell.setColspan(2);
		table.addCell(cell);
		
		cell = crearCelda("Consumo de materia seca predicho (kg MS/día)", "", false, true, false);
		cell.setColspan(4);
		table.addCell(cell);
		
		cell = crearCelda("CMSact (kg MS/día)", "", false, false, true);
		table.addCell(cell);
		
		cell = crearCelda(format.format(sumaCantidad), "", true, false, true);
		table.addCell(cell);
		
		cell = crearCelda("NRC (2001)", "", false, false, true);
		cell.setColspan(3);
		table.addCell(cell);
		
		cell = crearCelda(format.format(11.25), "", true, false, true);
		table.addCell(cell);
		
		cell = crearCelda("CMS de forraje (kg MS/día)", "", false, false, false);
		table.addCell(cell);
		
		cell = crearCelda(format.format(sumaForraje), "", true, false, false);
		table.addCell(cell);
		
		cell = crearCelda("NRC (2021) - Efectos animales", "", false, false, false);
		cell.setColspan(3);
		table.addCell(cell);
		
		cell = crearCelda(format.format(16.67), "", true, false, false);
		table.addCell(cell);
		
		cell = crearCelda("CMS de concentrado (kg MS/día)", "", false, false, true);
		table.addCell(cell);
		
		cell = crearCelda(format.format(sumaConcentrado), "", true, false, true);
		table.addCell(cell);
		
		cell = crearCelda("TAURI", "", false, false, true);
		cell.setColspan(3);
		table.addCell(cell);
		
		cell = crearCelda(format.format(20.91), "", true, false, true);
		table.addCell(cell);
		
		cell = crearCelda("Precio de la dieta ($/kg MS)", "", false, false, false);
		table.addCell(cell);
		
		if(sumaCantidad > 0) {
			cell = crearCelda(format.format(sumaPrecio / sumaCantidad), "", true, false, false);
		} else {
			cell = crearCelda(format.format(sumaPrecio), "", true, false, false);
		}
		table.addCell(cell);
		
		cell = crearCelda("", "", false, false, false);
		cell.setColspan(3);
		table.addCell(cell);
	}

	private void agregarBalance(PdfPTable table) {
		ModeloDto modelo = modeloService.calcularModelo(entradaDto, dieta);

		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
		decimalFormatSymbols.setDecimalSeparator(',');
		decimalFormatSymbols.setGroupingSeparator('.');
		DecimalFormat format = new DecimalFormat(FORMAT_NUMBER, decimalFormatSymbols);
		
		table.addCell(crearCelda("Mantenimiento", "N", false, false, true));
		table.addCell(crearCelda(format.format(modelo.getNeMaint()), "N", true, false, true));
		table.addCell(crearCelda(format.format(modelo.getMpMaint()), "N", true, false, true));
		table.addCell(crearCelda(format.format(modelo.getFecalCalcium() + modelo.getUrinaryCalcium()), "N", true, false, true));
		table.addCell(crearCelda(format.format(modelo.getFecalPhosphorous() + modelo.getUrinaryPhosphorous()), "N", true, false, true));
		table.addCell(crearCelda(format.format(modelo.getkFecal() + modelo.getkUrinary()), "N", true, false, true));
		table.addCell(crearCelda(format.format(modelo.getMgFecal() + modelo.getMgUrinary()), "N", true, false, true));
		
		table.addCell(crearCelda("Crecimiento", "N", false, false, false));
		table.addCell(crearCelda(format.format(modelo.getGrowht()), "N", true, false, false));
		table.addCell(crearCelda(format.format(modelo.getMpGrowth()), "N", true, false, false));
		table.addCell(crearCelda(format.format(modelo.getGrowthCalcium()), "N", true, false, false));
		table.addCell(crearCelda(format.format(modelo.getGrowthPhosphorous()), "N", true, false, false));
		table.addCell(crearCelda(format.format(modelo.getkGrowth()), "N", true, false, false));
		table.addCell(crearCelda(format.format(modelo.getMgGrowth()), "N", true, false, false));
		
		table.addCell(crearCelda("Lactancia", "N", false, false, true));
		table.addCell(crearCelda(format.format(modelo.getyEn()), "N", true, false, true));
		table.addCell(crearCelda(format.format(modelo.getMpLact()), "N", true, false, true));
		table.addCell(crearCelda(format.format(modelo.getLactationCalcium()), "N", true, false, true));
		table.addCell(crearCelda(format.format(modelo.getLactationPhosphorous()), "N", true, false, true));
		table.addCell(crearCelda(format.format(modelo.getkLactation()), "N", true, false, true));
		table.addCell(crearCelda(format.format(modelo.getMgLactation()), "N", true, false, true));
		
		table.addCell(crearCelda("Preñez", "N", false, false, false));
		table.addCell(crearCelda(format.format(modelo.getNePreg()), "N", true, false, false));
		table.addCell(crearCelda(format.format(modelo.getMpPreg()), "N", true, false, false));
		table.addCell(crearCelda(format.format(modelo.getPregnancyCalcium()), "N", true, false, false));
		table.addCell(crearCelda(format.format(modelo.getPregnancyPhosphorous()), "N", true, false, false));
		table.addCell(crearCelda(format.format(modelo.getkPregnancy()), "N", true, false, false));
		table.addCell(crearCelda(format.format(modelo.getMgPregnancy()), "N", true, false, false));
		
		table.addCell(crearCelda("Total requerido", "N", false, true, true));
		table.addCell(crearCelda(format.format(modelo.getTotalNeRequirement()), "N", true, true, true));
		table.addCell(crearCelda(format.format(modelo.getTotalMpRequirement()), "N", true, true, true));
		table.addCell(crearCelda(format.format(modelo.getCaRequirement()), "N", true, true, true));
		table.addCell(crearCelda(format.format(modelo.getpRequirement()), "N", true, true, true));
		table.addCell(crearCelda(format.format(modelo.getkRequirement()), "N", true, true, true));
		table.addCell(crearCelda(format.format(modelo.getMgRequirement()), "N", true, true, true));
		
		table.addCell(crearCelda("Total consumido", "N", false, true, false));
		table.addCell(crearCelda(format.format(modelo.getTotalConsumidoNel()), "N", true, true, false));
		table.addCell(crearCelda(format.format(modelo.getTotalConsumidoPm()), "N", true, true, false));
		table.addCell(crearCelda(format.format(modelo.getTotalConsumidoCa()), "N", true, true, false));
		table.addCell(crearCelda(format.format(modelo.getTotalConsumidoP()), "N", true, true, false));
		table.addCell(crearCelda(format.format(modelo.getTotalConsumidoK()), "N", true, true, false));
		table.addCell(crearCelda(format.format(modelo.getTotalConsumidoMg()), "N", true, true, false));
		
		table.addCell(crearCelda("Total consumido", "N", false, true, false));
		table.addCell(crearCelda(format.format(modelo.getBalanceNel()), "N", true, true, false));
		table.addCell(crearCelda(format.format(modelo.getBalancePm()), "N", true, true, false));
		table.addCell(crearCelda(format.format(modelo.getBalanceCa()), "N", true, true, false));
		table.addCell(crearCelda(format.format(modelo.getBalanceP()), "N", true, true, false));
		table.addCell(crearCelda(format.format(modelo.getBalanceK()), "N", true, true, false));
		table.addCell(crearCelda(format.format(modelo.getBalanceMg()), "N", true, true, false));
		
		table.addCell(crearCelda("% requerimiento", "N", false, true, false));
		table.addCell(crearCelda(format.format(modelo.getPorcentajeNel()), getColor(modelo.getPorcentajeNel()), true, true, false));
		table.addCell(crearCelda(format.format(modelo.getPorcentajePm()), getColor(modelo.getPorcentajePm()), true, true, false));
		table.addCell(crearCelda(format.format(modelo.getPorcentajeCa()), getColor(modelo.getPorcentajeCa()), true, true, false));
		table.addCell(crearCelda(format.format(modelo.getPorcentajeP()), getColor(modelo.getPorcentajeP()), true, true, false));
		table.addCell(crearCelda(format.format(modelo.getPorcentajeK()), getColor(modelo.getPorcentajeK()), true, true, false));
		table.addCell(crearCelda(format.format(modelo.getPorcentajeMg()), getColor(modelo.getPorcentajeMg()), true, true, false));
	}
	
	private String getColor(double valor) {
		String color = "V";
	    if(valor < -2.5) {
	      color = "R";
	    } else if(valor > 2.5) {
	      color = "A";
	    }
	    return color;
	}
	
	private PdfPCell crearCelda(String valor, String color, boolean numero, boolean negrilla, boolean fila) {
		Font font = crearFont(color, negrilla);
		PdfPCell cell = new PdfPCell(new Phrase(valor, font));
		setColorCelda(color, cell, fila);
		if(numero) {
			cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
		}
		return cell;
	}
	
	private PdfPCell crearCelda(String valor) {
		Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
		PdfPCell cell = new PdfPCell(new Phrase(valor, font));
		cell.setBorderColor(WebColors.getRGBColor(COLOR_SUBTITULO));
		cell.setBackgroundColor(WebColors.getRGBColor("#E7E7E7"));
		return cell;
	}
	
	
	private Font crearFont(String color, boolean negrilla) {
		Font font = null;
		if(negrilla) {
			font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
		} else {
			font = FontFactory.getFont(FontFactory.HELVETICA, 10);
		}
		if("V".equals(color)){
			font.setColor(WebColors.getRGBColor("#004225"));
		} else if ("R".equals(color)) {
			font.setColor(WebColors.getRGBColor("#61000b"));
		} else if ("A".equals(color)) {
			font.setColor(WebColors.getRGBColor("#5c2e00"));
		}
		return font;
	}

	private void setColorCelda(String color, PdfPCell cell, boolean fila) {
		if(fila) {
			cell.setBackgroundColor(WebColors.getRGBColor("#F4FEF9"));
			cell.setBorderColor(WebColors.getRGBColor(COLOR_SUBTITULO));
		} else if("V".equals(color)){
			cell.setBackgroundColor(WebColors.getRGBColor("#73e6ab"));
			cell.setBorderColor(WebColors.getRGBColor("#004225"));
		} else if ("R".equals(color)) {
			cell.setBorderColor(WebColors.getRGBColor("#61000b"));
			cell.setBackgroundColor(WebColors.getRGBColor("#ff858f"));
		} else if ("A".equals(color)) {
			cell.setBackgroundColor(WebColors.getRGBColor("#ffd278"));
			cell.setBorderColor(WebColors.getRGBColor("#5c2e00"));
		} else {
			cell.setBorderColor(WebColors.getRGBColor(COLOR_SUBTITULO));
		}
	}

	public void export(HttpServletResponse response, Integer idReporte) throws DocumentException, IOException {
		Document document = new Document(PageSize.A4);
		PdfWriter.getInstance(document, response.getOutputStream());		
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

		document.open();
		
		Image image = Image.getInstance("src/main/resources/logoTauri.png");
		image.scalePercent(60, 50);
		image.setAbsolutePosition(20, 770);
		document.add(image);
		
		document.addTitle("Reporte de simulación # " + idReporte);
		document.addCreator("TAURI");
		Paragraph saltoDeLinea = new Paragraph("\n");
		document.add(saltoDeLinea);
		document.add(crearTitulo(dateFormat.format(new Date()), 12, PdfPCell.ALIGN_RIGHT, false));
		document.add(crearTitulo("Reporte de simulación # " + idReporte, 18, ALIGN_CENTER, true));
		
		crearInformacionGeneral(document, idReporte);
		crearDieta(idReporte, document);
		crearBalance(document);
		crearEmision(document);
		crearBeneficio(document);

		document.close();
	}

	private void crearBeneficio(Document document) {
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
		decimalFormatSymbols.setDecimalSeparator(',');
		decimalFormatSymbols.setGroupingSeparator('.');
		DecimalFormat format = new DecimalFormat(FORMAT_NUMBER, decimalFormatSymbols);
		
		document.add(crearTitulo("Relación beneficio - costo", 15, ALIGN_CENTER, true));
		
		RelacionBeneficioCostoDto relacion = modeloService.calcularRelacionBeneficio(entradaDto, dieta);
		
		PdfPTable table = new PdfPTable(2);
		table.setWidthPercentage(100f);
		table.setWidths(new float[] {2f, 1f});
		table.setSpacingBefore(10);
		
		table.addCell(crearCelda("Eficiencia alimenticia (L/Kg MS)", "N", false, false, true));
		table.addCell(crearCelda(format.format(relacion.getEficienciaAlimentica()), "N", true, false, true));
		
		table.addCell(crearCelda("Costo de la dieta ($/kg MS)", "N", false, false, false));
		table.addCell(crearCelda(format.format(relacion.getCostoDieta()), "N", true, false, false));
		
		table.addCell(crearCelda("Costo/L leche", "N", false, false, true));
		table.addCell(crearCelda(format.format(relacion.getCostoLitroLeche()), "N", true, false, true));
		
		PdfPCell cell = crearCelda(" ", "", false, false, false);
		cell.setColspan(2);
		table.addCell(cell);
		
		table.addCell(crearCelda("Margen de utilidad bruta ($/L leche)", "N", false, false, true));
		table.addCell(crearCelda(format.format(relacion.getMargenUtilidadBruta()), "N", true, false, true));
		
		table.addCell(crearCelda("Margen porcentual (%)", "N", false, false, false));
		table.addCell(crearCelda(format.format(relacion.getMargenPorcentual()), "N", true, false, false));
		
		table.addCell(crearCelda("Relación precio de venta/Costo de alimentación", "N", false, false, true));
		table.addCell(crearCelda(format.format(relacion.getRelacionPrecioVentaCostoAlimentacion()), "N", true, false, true));
		
		document.add(table);
	}

	private void crearInformacionGeneral(Document document,Integer idReporte) {
		
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
		decimalFormatSymbols.setDecimalSeparator(',');
		decimalFormatSymbols.setGroupingSeparator('.');
		DecimalFormat format = new DecimalFormat(FORMAT_NUMBER, decimalFormatSymbols);
		
		entradaDto = entradaService.getEntrada(idReporte);
		
		document.add(crearTitulo("Información general", 15, ALIGN_CENTER, true));
		
		PdfPTable table = new PdfPTable(4);
		table.setWidthPercentage(100f);
		table.setWidths(new float[] { 2f, 2f, 2f, 2f});
		table.setSpacingBefore(10);
		
		table.addCell(crearCelda("Nombre de la simulación", "N", false, true, false));
		
		PdfPCell cell = crearCelda(entradaDto.getNombreReporte(), "N", false, false, false);
		cell.setColspan(3);
		table.addCell(cell);

		table.addCell(crearCelda("País", "N", false, true, false));
		
		cell = crearCelda("Colombia", "N", false, false, false);
		cell.setColspan(3);
		table.addCell(cell);
		
		table.addCell(crearCelda("Departamento/Estado", "N", false, true, false));
		
		cell = crearCelda(entradaDto.getDepartamento().getNombreDepartamento(), "N", false, false, false);
		cell.setColspan(3);
		table.addCell(cell);
		
		table.addCell(crearCelda("Municipio", "N", false, true, false));
		
		cell = crearCelda(entradaDto.getMunicipio().getMunicipio(), "N", false, false, false);
		cell.setColspan(3);
		table.addCell(cell);
		
		cell = crearCelda("Descripción del animal");
		cell.setColspan(4);
		table.addCell(cell);
		
		table.addCell(crearCelda("Raza", "N", false, true, false));
		table.addCell(crearCelda(entradaDto.getRaza(), "N", false, false, false));
		
		table.addCell(crearCelda("Tipo de animal", "N", false, true, false));
		table.addCell(crearCelda(entradaDto.getTipoAnimal(), "N", false, false, false));
		
		table.addCell(crearCelda("Peso corporal", "N", false, true, false));
		table.addCell(crearCelda(format.format(entradaDto.getPesoCorporal()), "N", true, false, false));
		
		table.addCell(crearCelda("Condición corporal", "N", false, true, false));
		table.addCell(crearCelda(String.valueOf(entradaDto.getCondicionCorporal().intValue()), "N", true, false, false));
		
		table.addCell(crearCelda("Días en leche", "N", false, true, false));
		table.addCell(crearCelda(String.valueOf(entradaDto.getDiasLeche().intValue()), "N", true, false, false));
		
		table.addCell(crearCelda("Días de preñez", "N", false, true, false));
		table.addCell(crearCelda(String.valueOf(entradaDto.getDiasPrenez().intValue()), "N", true, false, false));
		
		table.addCell(crearCelda("Número de partos", "N", false, true, false));
		table.addCell(crearCelda(String.valueOf(entradaDto.getNumeroParto().intValue()), "N", true, false, false));
		
		table.addCell(crearCelda("Intervalo entre partos", "N", false, true, false));
		table.addCell(crearCelda(String.valueOf(entradaDto.getIntervaloParto().intValue()), "N", true, false, false));
		
		cell = crearCelda("Producción");
		cell.setColspan(4);
		table.addCell(cell);
		
		cell = crearCelda("Composición de la leche", "N", false, true, false);
		cell.setColspan(4);
		table.addCell(cell);
		
		table.addCell(crearCelda("Grasa", "N", false, true, false));
		table.addCell(crearCelda("% " + format.format(entradaDto.getGrasa()), "N", true, false, false));
		
		table.addCell(crearCelda("Proteína cruda", "N", false, true, false));
		table.addCell(crearCelda("% " + format.format(entradaDto.getProteinaCruda()), "N", true, false, false));
		
		table.addCell(crearCelda("Lactosa", "N", false, true, false));
		table.addCell(crearCelda("% " + format.format(entradaDto.getLactosa()), "N", true, false, false));
		
		table.addCell(crearCelda("Precio de venta (Litro)", "N", false, true, false));
		table.addCell(crearCelda("$ " + format.format(entradaDto.getPrecioVenta()), "N", true, false, false));
		
		table.addCell(crearCelda("Producción de leche, litros/día", "N", false, true, false));
		table.addCell(crearCelda(String.valueOf(entradaDto.getProduccionLeche().intValue()) + " l", "N", true, false, false));
		
		table.addCell(crearCelda("Manejo", "N", false, true, false));
		table.addCell(crearCelda(entradaDto.getManejo(), "N", true, false, false));
		
		if("pastoreo".equals(entradaDto.getManejo())) {
			table.addCell(crearCelda("Distancia entre la sala ordeño y la pradera", "N", false, true, false));
			table.addCell(crearCelda(String.valueOf(entradaDto.getDistancia().intValue()) + " m", "N", true, false, false));
			
			table.addCell(crearCelda("Número de viajes", "N", false, true, false));
			table.addCell(crearCelda(String.valueOf(entradaDto.getNumeroViajes().intValue()), "N", true, false, false));
			
			table.addCell(crearCelda("Topografía", "N", false, true, false));
			cell = crearCelda(entradaDto.getTipografia(), "N", false, false, false);
			cell.setColspan(3);
			table.addCell(cell);
		}
		
		document.add(table);
	}

	private void crearBalance(Document document) {
		document.add(crearTitulo("Balance", 15, ALIGN_CENTER, true));

		PdfPTable table = new PdfPTable(7);
		table.setWidthPercentage(100f);
		table.setWidths(new float[] {2f, 1f, 1f, 1f, 1f, 1f, 1f });
		table.setSpacingBefore(10);

		crearBalanceHeader(table);
		agregarBalance(table);
		document.add(table);
	}
	
	private void crearEmision(Document document) {
		
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
		decimalFormatSymbols.setDecimalSeparator(',');
		decimalFormatSymbols.setGroupingSeparator('.');
		DecimalFormat format = new DecimalFormat(FORMAT_NUMBER, decimalFormatSymbols);
		
		document.add(crearTitulo("Emisión GEI", 15, ALIGN_CENTER, true));
		
		document.add(crearTitulo("Gases de origen entérico", 12, ALIGN_CENTER, true));
		
		EmisionGeiDto emisionGeiDto = modeloService.calcularEmisionGei(entradaDto, dieta);

		PdfPTable table = new PdfPTable(3);
		table.setWidthPercentage(100f);
		table.setWidths(new float[] {2f, 1f, 1f});
		table.setSpacingBefore(10);

		crearEmisionHeader(table, "METANO (CH4)", true);
		crearEmisionValue(table, CH4_DIA, format.format(emisionGeiDto.getMetanoDiaGramo()), format.format(emisionGeiDto.getMetanoDiaLitro()), true);
		crearEmisionValue(table, CH4_KG_MS_CONSUMIDA, format.format(emisionGeiDto.getMetanoMsConsumidoGramo()), format.format(emisionGeiDto.getMetanoMsConsumidaLitro()), false);
		crearEmisionValue(table, CH4_KG_LCG4_0, format.format(emisionGeiDto.getMetanoLcgGramo()), format.format(emisionGeiDto.getMetanoLcgLitro()), true);
		crearEmisionValue(table, FACTOR_DE_EMISIÓN_KG_ANIMAL, format.format(emisionGeiDto.getMetanoFactorEmision()), "", false);
		crearEmisionValue(table, "Ym (Energía bruta perdida en forma de metano, %)", format.format(emisionGeiDto.getMetanoYm()), "", true);
		
		PdfPCell cell = crearCelda(" ", "", false, false, false);
		cell.setColspan(3);
		table.addCell(cell);
		
		crearEmisionHeader(table, "DIÓXIDO DE CARBONO (CO2)", true);
		crearEmisionValue(table, CO2_DIA, format.format(emisionGeiDto.getDioxidoCarbonoDiaGramo()), format.format(emisionGeiDto.getDioxidoCarbonoDiaLitro()), true);
		crearEmisionValue(table, CO2_KG_MS_CONSUMIDA, format.format(emisionGeiDto.getDioxidoCarbonoMsConsumidoGramo()), format.format(emisionGeiDto.getDioxidoCarbonoMsConsumidoLitro()), false);
		crearEmisionValue(table, CO2_KG_LCG4_0, format.format(emisionGeiDto.getDioxidoCarbonoLcgGramo()), format.format(emisionGeiDto.getDioxidoCarbonoLcgLitro()), true);
		crearEmisionValue(table, FACTOR_DE_EMISIÓN_KG_ANIMAL, format.format(emisionGeiDto.getDioxidoCarbonoFactorEmision()), "", false);
		
		cell = crearCelda(" ", "", false, false, false);
		cell.setColspan(3);
		table.addCell(cell);
		
		crearEmisionHeader(table, "DIÓXIDO DE CARBONO EQUIVALENTE (CO2eq)", false);
		crearEmisionValue(table, CO2_DIA, null, format.format(emisionGeiDto.getDioxidoCarbonoEqDiaGramo()), true);
		crearEmisionValue(table, CO2_KG_MS_CONSUMIDA, null, format.format(emisionGeiDto.getDioxidoCarbonoEqMsConsumidoGramo()), false);
		crearEmisionValue(table, CO2_KG_LCG4_0, null, format.format(emisionGeiDto.getDioxidoCarbonoEqLcgGramo()), true);
		crearEmisionValue(table, FACTOR_DE_EMISIÓN_KG_ANIMAL, null, format.format(emisionGeiDto.getDioxidoCarbonoEqFactorEmision()), false);
		
		document.add(table);
		
		document.add(crearTitulo("Gases desde las excretas en el suelo", 12, ALIGN_CENTER, true));
		
		table = new PdfPTable(3);
		table.setWidthPercentage(100f);
		table.setWidths(new float[] {2f, 1f, 1f});
		table.setSpacingBefore(10);

		crearEmisionHeader(table, "METANO (CH4)", false);
		crearEmisionValue(table, CH4_DIA, null, format.format(emisionGeiDto.getMetanoDiaExcretaGramo()), true);
		crearEmisionValue(table, CH4_KG_MS_CONSUMIDA, null, format.format(emisionGeiDto.getMetanoMsConsumidoExcretaGramo()), false);
		crearEmisionValue(table, CH4_KG_LCG4_0, null, format.format(emisionGeiDto.getMetanoLcgExcretaGramo()), true);
		crearEmisionValue(table, FACTOR_DE_EMISIÓN_KG_ANIMAL, null, format.format(emisionGeiDto.getMetanoExcretaFactorEmision()), false);
		
		cell = crearCelda(" ", "", false, false, false);
		cell.setColspan(3);
		table.addCell(cell);
		
		crearEmisionHeader(table, "DIÓXIDO DE CARBONO (CO2)", false);
		crearEmisionValue(table, CO2_DIA, null, format.format(emisionGeiDto.getDioxidoCarbonoExcretaDiaGramo()), true);
		crearEmisionValue(table, CO2_KG_MS_CONSUMIDA, null, format.format(emisionGeiDto.getDioxidoCarbonoExcretaMsConsumidoGramo()), false);
		crearEmisionValue(table, CO2_KG_LCG4_0, null, format.format(emisionGeiDto.getDioxidoCarbonoExcretaLgcGramo()), true);
		crearEmisionValue(table, FACTOR_DE_EMISIÓN_KG_ANIMAL, null, format.format(emisionGeiDto.getDioxidoCarbonoExcretaFactorEmision()), false);
		
		cell = crearCelda(" ", "", false, false, false);
		cell.setColspan(3);
		table.addCell(cell);
		
		crearEmisionHeader(table, "DIÓXIDO DE CARBONO EQUIVALENTE (CO2eq)", false);
		crearEmisionValue(table, CO2_DIA, null, format.format(emisionGeiDto.getCo2EqDiaGramo()), true);
		crearEmisionValue(table, CO2_KG_MS_CONSUMIDA, null, format.format(emisionGeiDto.getCo2EqMsConsumidaGramo()), false);
		crearEmisionValue(table, CO2_KG_LCG4_0, null, format.format(emisionGeiDto.getCo2EqLgcGramo()), true);
		crearEmisionValue(table, FACTOR_DE_EMISIÓN_KG_ANIMAL, null, format.format(emisionGeiDto.getCo2FactorEmision()), false);
		
		cell = crearCelda(" ", "", false, false, false);
		cell.setColspan(3);
		table.addCell(cell);
		
		crearEmisionHeader(table, "ÓXIDO NITROSO (N2O)", false);
		crearEmisionValue(table, "N2O/día", null, format.format(emisionGeiDto.getOxidoNitrosoDiaGramo()), true);
		crearEmisionValue(table, "N2O/kg MS consumida", null, format.format(emisionGeiDto.getOxidoNitrosoMsConsumidoGramo()), false);
		crearEmisionValue(table, "N2O/kg LCG4.0%", null, format.format(emisionGeiDto.getOxidoNitrosoLcgGramo()), true);
		crearEmisionValue(table, FACTOR_DE_EMISIÓN_KG_ANIMAL, null, format.format(emisionGeiDto.getOxidoNitrosoFactorEmision()), false);
		
		document.add(table);		
		
	}

	private void crearDieta(Integer idReporte, Document document) {
		document.add(crearTitulo("Dieta", 15, ALIGN_CENTER, true));
		
		PdfPTable table = new PdfPTable(6);
		table.setWidthPercentage(100f);
		table.setWidths(new float[] { 4.5f, 3f, 2f, 2f, 2f, 2f });
		table.setSpacingBefore(10);

		crearDietaHeader(table);
		agregarAlimentos(table, idReporte);

		document.add(table);
	}
	
	public Paragraph crearTitulo(String valor, int size, int aling, boolean negrilla) {
		Font font = null;
		if(negrilla) {
			font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, size);
		} else {
			font = FontFactory.getFont(FontFactory.HELVETICA, size);
		}
		Paragraph p = new Paragraph(valor, font);
		p.setAlignment(aling);
		return p;
	}
}
