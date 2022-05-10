package co.com.udea.tauri.rest;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.lowagie.text.Rectangle;
import com.lowagie.text.html.WebColors;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPage;
import com.lowagie.text.pdf.PdfWriter;

import co.com.udea.tauri.dtos.DietaDto;
import co.com.udea.tauri.dtos.EntradaDto;
import co.com.udea.tauri.services.impl.DietaService;
import co.com.udea.tauri.services.impl.EntradaService;

@RestController
@RequestMapping("api")
@CrossOrigin(origins = "*")
public class ReporteRest {

	private static final String COLOR_SUBTITULO = "#ced4da";
	private static final String formatTest = "#,##0.00";
	private static final int ALIGN_CENTER = Paragraph.ALIGN_CENTER;
	private static final String G_DIA = "(g/día)";
	
	@Autowired
	private DietaService dietaService;
	
	@Autowired
	private EntradaService entradaService;

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

	private void agregarAlimentos(PdfPTable table, Integer idReporte) {
		List<DietaDto> dieta = dietaService.listarDieta(idReporte);

		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
		decimalFormatSymbols.setDecimalSeparator(',');
		decimalFormatSymbols.setGroupingSeparator('.');
		DecimalFormat format = new DecimalFormat(formatTest, decimalFormatSymbols);
		
		int contador = 0;
		for (DietaDto dietaDto : dieta) {
			table.addCell(crearCelda(dietaDto.getBiblioteca().getNombre(), "N", false, false, contador % 2 == 0));
			table.addCell(crearCelda(dietaDto.getBiblioteca().getTipo(), "N", false, false, contador % 2 == 0));
			table.addCell(crearCelda(format.format(dietaDto.getCantidad()), "N", true, false, contador % 2 == 0));
			table.addCell(crearCelda(format.format(dietaDto.getCantidadOfrecido()), "N", true, false, contador % 2 == 0));
			table.addCell(crearCelda("$ " + format.format(dietaDto.getPrecio()), "N", true, false, contador % 2 == 0));
			contador += 1;
		}
	}

	private void agregarBalance(PdfPTable table) {

		List<String> lista = new ArrayList<>();
		lista.add("Mantenimiento");
		lista.add("Crecimiento");
		lista.add("Lactancia");
		lista.add("Preñez");
		lista.add("Total requerido");
		lista.add("Total consumido");
		lista.add("Balance");

		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
		decimalFormatSymbols.setDecimalSeparator(',');
		decimalFormatSymbols.setGroupingSeparator('.');
		DecimalFormat format = new DecimalFormat(formatTest, decimalFormatSymbols);
		
		for (int i = 0; i < 7; i++) {
			table.addCell(crearCelda(lista.get(i), "N", false, i > 3, i % 2 == 0));
			table.addCell(crearCelda(format.format(9.5), "N", true, i > 3, i % 2 == 0));
			table.addCell(crearCelda(format.format(880.9), "N", true, i > 3, i % 2 == 0));
			table.addCell(crearCelda(format.format(18.8), "N", true, i > 3, i % 2 == 0));
			table.addCell(crearCelda(format.format(21.2), "N", true, i > 3, i % 2 == 0));
			table.addCell(crearCelda(format.format(175.0), "N", true, i > 3, i % 2 == 0));
			table.addCell(crearCelda(format.format(1.8), "N", true, i > 3, i % 2 == 0));
		}
		
		
		table.addCell(crearCelda("% requerimiento", "N", false, true, false));
		table.addCell(crearCelda("0,00", "V", true, true, false));
		table.addCell(crearCelda("0,00", "R", true, true, false));
		table.addCell(crearCelda("0,00", "R", true, true, false));
		table.addCell(crearCelda("0,00", "A", true, true, false));
		table.addCell(crearCelda("0,00", "A", true, true, false));
		table.addCell(crearCelda("0,00", "R", true, true, false));
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
			cell.setBackgroundColor(WebColors.getRGBColor("#ECFAE9"));
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
		
		Image image = Image.getInstance("src/main/resources/logo-udea.png");
		image.scalePercent(60, 50);
		image.setAbsolutePosition(20, 770);
		document.add(image);
		
		image = Image.getInstance("src/main/resources/logo-poli.png");
		image.scalePercent(60, 50);
		image.setAbsolutePosition(350, 790);
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

		document.close();
	}

	private void crearInformacionGeneral(Document document,Integer idReporte) {
		
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
		decimalFormatSymbols.setDecimalSeparator(',');
		decimalFormatSymbols.setGroupingSeparator('.');
		DecimalFormat format = new DecimalFormat(formatTest, decimalFormatSymbols);
		
		EntradaDto entrada = entradaService.getEntrada(idReporte);
		
		document.add(crearTitulo("Información general", 15, ALIGN_CENTER, true));
		
		PdfPTable table = new PdfPTable(4);
		table.setWidthPercentage(100f);
		table.setWidths(new float[] { 2f, 2f, 2f, 2f});
		table.setSpacingBefore(10);
		
		table.addCell(crearCelda("Nombre de la simulación", "N", false, true, false));
		
		PdfPCell cell = crearCelda(entrada.getNombreReporte(), "N", false, false, false);
		cell.setColspan(3);
		table.addCell(cell);

		table.addCell(crearCelda("País", "N", false, true, false));
		
		cell = crearCelda("Colombia", "N", false, false, false);
		cell.setColspan(3);
		table.addCell(cell);
		
		table.addCell(crearCelda("Departamento/Estado", "N", false, true, false));
		
		cell = crearCelda(entrada.getDepartamento().getNombreDepartamento(), "N", false, false, false);
		cell.setColspan(3);
		table.addCell(cell);
		
		table.addCell(crearCelda("Municipio", "N", false, true, false));
		
		cell = crearCelda(entrada.getMunicipio().getMunicipio(), "N", false, false, false);
		cell.setColspan(3);
		table.addCell(cell);
		
		cell = crearCelda("Descripción del animal");
		cell.setColspan(4);
		table.addCell(cell);
		
		table.addCell(crearCelda("Raza", "N", false, true, false));
		table.addCell(crearCelda(entrada.getRaza(), "N", false, false, false));
		
		table.addCell(crearCelda("Tipo de animal", "N", false, true, false));
		table.addCell(crearCelda(entrada.getTipoAnimal(), "N", false, false, false));
		
		table.addCell(crearCelda("Peso corporal", "N", false, true, false));
		table.addCell(crearCelda(format.format(entrada.getPesoCorporal()), "N", true, false, false));
		
		table.addCell(crearCelda("Condición corporal", "N", false, true, false));
		table.addCell(crearCelda(String.valueOf(entrada.getCondicionCorporal().intValue()), "N", true, false, false));
		
		table.addCell(crearCelda("Días en leche", "N", false, true, false));
		table.addCell(crearCelda(String.valueOf(entrada.getDiasLeche().intValue()), "N", true, false, false));
		
		table.addCell(crearCelda("Días de preñez", "N", false, true, false));
		table.addCell(crearCelda(String.valueOf(entrada.getDiasPrenez().intValue()), "N", true, false, false));
		
		table.addCell(crearCelda("Número de partos", "N", false, true, false));
		table.addCell(crearCelda(String.valueOf(entrada.getNumeroParto().intValue()), "N", true, false, false));
		
		table.addCell(crearCelda("Intervalo entre partos", "N", false, true, false));
		table.addCell(crearCelda(String.valueOf(entrada.getIntervaloParto().intValue()), "N", true, false, false));
		
		cell = crearCelda("Producción");
		cell.setColspan(4);
		table.addCell(cell);
		
		cell = crearCelda("Composición de la leche", "N", false, true, false);
		cell.setColspan(4);
		table.addCell(cell);
		
		table.addCell(crearCelda("Grasa", "N", false, true, false));
		table.addCell(crearCelda("% " + format.format(entrada.getGrasa()), "N", true, false, false));
		
		table.addCell(crearCelda("Proteína cruda", "N", false, true, false));
		table.addCell(crearCelda("% " + format.format(entrada.getProteinaCruda()), "N", true, false, false));
		
		table.addCell(crearCelda("Lactosa", "N", false, true, false));
		table.addCell(crearCelda("% " + format.format(entrada.getLactosa()), "N", true, false, false));
		
		table.addCell(crearCelda("Precio de venta (Litro)", "N", false, true, false));
		table.addCell(crearCelda("$ " + format.format(entrada.getPrecioVenta()), "N", true, false, false));
		
		table.addCell(crearCelda("Producción de leche, litros/día", "N", false, true, false));
		table.addCell(crearCelda(String.valueOf(entrada.getProduccionLeche().intValue()) + " l", "N", true, false, false));
		
		table.addCell(crearCelda("Manejo", "N", false, true, false));
		table.addCell(crearCelda(entrada.getManejo(), "N", true, false, false));
		
		if("pastoreo".equals(entrada.getManejo())) {
			table.addCell(crearCelda("Distancia entre la sala ordeño y la pradera", "N", false, true, false));
			table.addCell(crearCelda(String.valueOf(entrada.getDistancia().intValue()) + " m", "N", true, false, false));
			
			table.addCell(crearCelda("Número de viajes", "N", false, true, false));
			table.addCell(crearCelda(String.valueOf(entrada.getNumeroViajes().intValue()), "N", true, false, false));
			
			table.addCell(crearCelda("Topografía", "N", false, true, false));
			cell = crearCelda(entrada.getTipografia(), "N", false, false, false);
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

	private void crearDieta(Integer idReporte, Document document) {
		document.add(crearTitulo("Dieta", 15, ALIGN_CENTER, true));
		
		PdfPTable table = new PdfPTable(5);
		table.setWidthPercentage(100f);
		table.setWidths(new float[] { 4.5f, 3f, 2f, 2f, 2f });
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
