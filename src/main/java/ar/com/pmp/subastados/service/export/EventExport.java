package ar.com.pmp.subastados.service.export;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.com.pmp.subastados.domain.Bid;
import ar.com.pmp.subastados.domain.Event;
import ar.com.pmp.subastados.domain.Lote;
import ar.com.pmp.subastados.domain.User;
import ar.com.pmp.subastados.repository.BidRepository;
import ar.com.pmp.subastados.repository.EventRepository;
import ar.com.pmp.subastados.web.rest.errors.EntityNotFoundException;

@Service
public class EventExport {

	@Autowired
	private EventRepository eventRepository;
	@Autowired
	private BidRepository bidRepository;

	public Workbook buildExcelDocument(Long eventId) throws Exception {
		Event event = eventRepository.findOne(eventId);
		if (event == null)
			throw new EntityNotFoundException("Evento " + eventId + " no encontrado");

		Workbook wb = new HSSFWorkbook();
		Sheet sheet = wb.createSheet(event.getName() + "_" + eventId);

		sheet.setDisplayGridlines(false);
		sheet.setPrintGridlines(false);
		sheet.setFitToPage(true);
		sheet.setHorizontallyCenter(true);
		PrintSetup printSetup = sheet.getPrintSetup();
		printSetup.setLandscape(true);
		sheet.setAutobreaks(true);
		printSetup.setFitHeight((short) 1);
		printSetup.setFitWidth((short) 1);

		// CellStyle dateStyle = getDateStyle(wb);
		CellStyle headerStyle = getHeaderStyle(wb);
		CellStyle subHeaderStyle = getSubHeaderStyle(wb);
		CellStyle style = getStyle(wb);

		Cell cell = null;

		// Nombre
		Row row = sheet.createRow(0);
		cell = getCell(row, 0);
		setCellValue(cell, "Nombre");
		setCellStyle(headerStyle, cell);

		cell = getCell(row, 1);
		setCellValue(cell, event.getName());
		setCellStyle(style, cell);

		// Fecha inicio
		row = sheet.createRow(1);
		cell = getCell(row, 0);
		setCellValue(cell, "Fecha Inicio");
		setCellStyle(headerStyle, cell);

		cell = getCell(row, 1);
		setCellValue(cell, formatDate(event.getInitDate()));
		// setCellStyle(headerStyle, cell);

		// Fecha fin
		row = sheet.createRow(2);
		cell = getCell(row, 0);
		setCellValue(cell, "Fecha Fin");
		setCellStyle(headerStyle, cell);

		cell = getCell(row, 1);
		setCellValue(cell, formatDate(event.getEndDate()));
		// setCellStyle(headerStyle, cell);

		int counter = 4;
		ArrayList<Lote> lotes = new ArrayList<>(event.getLotes());
		Collections.sort(lotes);

		for (Lote lote : lotes) {
			createLoteHeader(sheet, counter++, headerStyle);

			row = sheet.createRow(counter++);

			cell = getCell(row, 0);
			setCellValue(cell, lote.getId());
			setCellStyle(style, cell);

			cell = getCell(row, 1);
			setCellValue(cell, lote.getIdcaballo());
			setCellStyle(style, cell);

			cell = getCell(row, 2);
			setCellValue(cell, lote.getOrden());
			setCellStyle(style, cell);

			cell = getCell(row, 3);
			setCellValue(cell, lote.getProduct().getNombre());
			setCellStyle(style, cell);

			cell = getCell(row, 4);
			setCellValue(cell, lote.getInitialPrice());

			List<Bid> bids = bidRepository.findByLoteIdOrderByDateDesc(lote.getId());
			if (!bids.isEmpty())
				createBidHeader(sheet, counter++, subHeaderStyle);

			for (Bid bid : bids) {

				row = sheet.createRow(counter++);

				cell = getCell(row, 1);
				setCellValue(cell, formatDate(bid.getDate()));
				// setCellStyle(style, cell);

				User user = bid.getUser();

				cell = getCell(row, 2);
				setCellValue(cell, user.getFirstName() + " " + user.getLastName());
				setCellStyle(style, cell);

				cell = getCell(row, 3);
				setCellValue(cell, user.getCellPhone());
				setCellStyle(style, cell);

				cell = getCell(row, 4);
				setCellValue(cell, bid.getPrice());
			}

			counter++;
		}

		autoSizeColumn(sheet, 5);

		return wb;
	}

	private String formatDate(Instant instant) {
		return instant.atZone(ZoneOffset.systemDefault()).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
	}

	private void setCellStyle(CellStyle style, Cell cell) {
		cell.setCellStyle(style);
	}

	private Cell getCell(Row row, int column) {
		Cell cell = row.getCell(column);
		if (cell == null)
			return row.createCell(column);
		return cell;
	}

	private void setCellValue(Cell cell, Object value) {
		if (value == null)
			return;

		if (value instanceof Double)
			cell.setCellValue(((Double) value).doubleValue());
		else if (value instanceof Date)
			cell.setCellValue((Date) value);
		else
			cell.setCellValue(value.toString());

	}

	private void createLoteHeader(Sheet sheet, int rowNumber, CellStyle headerStyle) {
		Row row = sheet.createRow(rowNumber);

		Cell cell = getCell(row, 0);
		setCellValue(cell, "Lote Id");
		setCellStyle(headerStyle, cell);

		cell = getCell(row, 1);
		setCellValue(cell, "Id Caballo");
		setCellStyle(headerStyle, cell);

		cell = getCell(row, 2);
		setCellValue(cell, "Orden");
		setCellStyle(headerStyle, cell);

		cell = getCell(row, 3);
		setCellValue(cell, "Nombre");
		setCellStyle(headerStyle, cell);

		cell = getCell(row, 4);
		setCellValue(cell, "Precio Inicial");
		setCellStyle(headerStyle, cell);
	}

	private void createBidHeader(Sheet sheet, int rowNumber, CellStyle subHeaderStyle) {
		Row row = sheet.createRow(rowNumber);

		Cell cell = getCell(row, 1);
		setCellValue(cell, "Fecha");
		setCellStyle(subHeaderStyle, cell);

		cell = getCell(row, 2);
		setCellValue(cell, "Nombre");
		setCellStyle(subHeaderStyle, cell);

		cell = getCell(row, 3);
		setCellValue(cell, "Celular");
		setCellStyle(subHeaderStyle, cell);

		cell = getCell(row, 4);
		setCellValue(cell, "Oferta");
		setCellStyle(subHeaderStyle, cell);
	}

	protected CellStyle getHeaderStyle(Workbook wb) {
		CellStyle style = wb.createCellStyle();
		style.setFillForegroundColor(HSSFColor.LIGHT_CORNFLOWER_BLUE.index);
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setAlignment(HorizontalAlignment.RIGHT);
		return style;
	}

	protected CellStyle getSubHeaderStyle(Workbook wb) {
		CellStyle style = wb.createCellStyle();
		style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setAlignment(HorizontalAlignment.RIGHT);
		return style;
	}

	protected CellStyle getStyle(Workbook wb) {
		CellStyle style = wb.createCellStyle();
		style.setAlignment(HorizontalAlignment.RIGHT);
		return style;
	}

	protected CellStyle getDateStyle(Workbook wb) {
		DataFormat df = wb.createDataFormat();
		CellStyle style = wb.createCellStyle();
		style.setDataFormat(df.getFormat("dd/MM/yyyy HH:mm:ss"));
		style.setAlignment(HorizontalAlignment.RIGHT);
		return style;
	}

	protected void autoSizeColumn(Sheet sheet, int colSize) {
		for (int j = 0; j < colSize; j++) {
			sheet.autoSizeColumn(j);
		}
	}
}
