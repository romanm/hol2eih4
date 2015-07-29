package hol2eih4;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("excelService")
public class ExcelService {
	
	private static final Logger logger = LoggerFactory.getLogger(ExcelService.class);
		String monthTemplateName = "month_template";
	
	HSSFSheet monthTemplateSheet;
	
	public void createExcel(List<Map<String, Object>> moveTodayPatientsList, DateTime dateTime) {
//		testExcel(moveTodayPatientsList);
		HSSFWorkbook pyx2015 = readExcel();
		createHelper = pyx2015.getCreationHelper();
		dateCellStyle = pyx2015.createCellStyle();
		dateCellStyle.setDataFormat(
				createHelper.createDataFormat().getFormat("dd.mm.yyyy"));

		logger.debug(""+pyx2015.getNumberOfSheets());
		logger.debug(""+pyx2015.getSheet(monthTemplateName).getSheetName());
		HSSFSheet monthSheet = getMonthSheet(pyx2015, dateTime);
		monthTemplateSheet = pyx2015.getSheet(monthTemplateName);
		Integer rowNr = findFirstDepartmentRow(monthTemplateSheet);
		String stringCellValue = monthTemplateSheet.getRow(rowNr).getCell(0).getStringCellValue();
		for (Map<String, Object> map : moveTodayPatientsList) {
			setRCIntegerValue(monthTemplateSheet,rowNr,1,parseInt(map, "DEPARTMENT_BED"));
			setRCIntegerValue(monthTemplateSheet,rowNr,2,parseInt(map, "MOVEDEPARTMENTPATIENT_PATIENT1DAY"));
			setRCIntegerValue(monthTemplateSheet,rowNr,3,parseInt(map, "MOVEDEPARTMENTPATIENT_IN"));
			setRCIntegerValue(monthTemplateSheet,rowNr,4,parseInt(map, "MOVEDEPARTMENTPATIENT_INDEPARTMENT"));
			setRCIntegerValue(monthTemplateSheet,rowNr,6,parseInt(map, "MOVEDEPARTMENTPATIENT_OUTDEPARTMENT"));
			setRCIntegerValue(monthTemplateSheet,rowNr,8,parseInt(map, "MOVEDEPARTMENTPATIENT_OUT"));
			setRCIntegerValue(monthTemplateSheet,rowNr,9,parseInt(map, "MOVEDEPARTMENTPATIENT_DEAD"));
			setRCIntegerValue(monthTemplateSheet,rowNr,14,parseInt(map, "MOVEDEPARTMENTPATIENT_SITY"));
			setRCIntegerValue(monthTemplateSheet,rowNr,15,parseInt(map, "MOVEDEPARTMENTPATIENT_LYING"));
			setRCIntegerValue(monthTemplateSheet,rowNr,16,parseInt(map, "MOVEDEPARTMENTPATIENT_CHILD"));
			setRCIntegerValue(monthTemplateSheet,rowNr,17,parseInt(map, "MOVEDEPARTMENTPATIENT_INSURED"));
			setRCIntegerValue(monthTemplateSheet,rowNr,18,parseInt(map, "MOVEDEPARTMENTPATIENT_CAES"));
			rowNr++;
		}
		saveExcel(pyx2015,AppConfig.applicationExcelFolderPfad+"pyx2015.xls");
		findDate(monthTemplateSheet);
	}

	private HSSFSheet getMonthSheet(HSSFWorkbook pyx2015, DateTime dateTime) {
		int monthOfYear = dateTime.getMonthOfYear();
		HSSFSheet monthSheet = null;
		int monthSheetNr = 0;
		int indexOfScheet = 0;
		//find monthScheet
		for (; indexOfScheet < pyx2015.getNumberOfSheets(); indexOfScheet++) {
			if(pyx2015.getSheetName(indexOfScheet).matches("\\d+"))
			{
				monthSheetNr = Integer.parseInt(pyx2015.getSheetName(indexOfScheet));
				if(monthSheetNr == monthOfYear)
				{
					monthSheet = pyx2015.getSheetAt(indexOfScheet);
					pyx2015.setActiveSheet(indexOfScheet);
					break;
				}
			}
		}
		//create new monthSheet
		if(monthSheetNr != monthOfYear)
		{
			monthSheet = pyx2015.createSheet((monthOfYear<10?"0":"")+monthOfYear);
			int numberOfSheets = pyx2015.getNumberOfSheets();
			pyx2015.setSheetOrder(monthTemplateName, numberOfSheets - 1);
			pyx2015.setActiveSheet(numberOfSheets - 2);
		}
		//init month in new sheet
		int lastRowNum = monthSheet.getLastRowNum();
		if(lastRowNum == 0)
		{
			DateTime minDate = dateTime.dayOfMonth().withMinimumValue();
			DateTime maxDate = dateTime.dayOfMonth().withMaximumValue();

			int row = 1;

			while (minDate.monthOfYear().get() == maxDate.monthOfYear().get()) {
				HSSFCell createCell = monthSheet.createRow(row++).createCell(0);
				createCell.setCellStyle(dateCellStyle);
				createCell.setCellFormula("DATE("
						+ minDate.getYear()
						+ ","
						+ minDate.getMonthOfYear()
						+ ","
						+ minDate.getDayOfMonth()
						+ ")");
				minDate = minDate.plusDays(1);
				row++;
			}
		}
		//active day
		
		logger.debug(""+dateTime);
		HSSFCell dateCell = getDateCell(dateTime.dayOfMonth().get(), monthSheet);
		logger.debug(""+dateCell.getRowIndex());
		dateCell.setAsActiveCell();
		HSSFCell nextDateCell = getDateCell(dateTime.dayOfMonth().get()+1, monthSheet);
		if(nextDateCell.getRowIndex()-dateCell.getRowIndex() < 31)
		{
			monthSheet.shiftRows(dateCell.getRowIndex() + 1, dateCell.getSheet().getLastRowNum(), 31);
		}
		HSSFCell monthTemplateDateCell = pyx2015.getSheet(monthTemplateName).getRow(1).getCell(0);
		copyWithStyle(getCell(monthTemplateDateCell, 1, 0), initCell(dateCell, 1, 0));
		int addRowShift = 3;
		for (int i = 0; i < 23; i++) {
			copyWithStyle(getCell(monthTemplateDateCell, addRowShift + i, 0), initCell(dateCell, addRowShift + i, 0));
		}
		int addColShift = 1;
		for (int i = 0; i < 18; i++) {
//			copyValue(getCell(monthTemplateDateCell, 1, addColShift + i), initCell(dateCell, 1, addColShift + i));
			copyWithStyle(getCell(monthTemplateDateCell, 1, addColShift + i), initCell(dateCell, 1, addColShift + i));
		}
		dateCell.getSheet().autoSizeColumn(dateCell.getColumnIndex());
		dateCell.getSheet().autoSizeColumn(dateCell.getColumnIndex());
		dateCell.getSheet().getRow(dateCell.getRowIndex()+1).setHeight((short)1400);
		return monthSheet;
	}

	private void copyWithStyle(HSSFCell sourceCell, HSSFCell destinationCell) {
		copyValue(sourceCell, destinationCell);
		destinationCell.setCellStyle(sourceCell.getCellStyle());
	}

	private void copyValue(HSSFCell sourceCell, HSSFCell destinationCell) {
		switch(sourceCell.getCellType()) {
		case HSSFCell.CELL_TYPE_STRING:
			destinationCell.setCellValue(sourceCell.getStringCellValue());
			break;
		}
	}

	private HSSFCell getCell(HSSFCell startCell, int rowSkip, int columnSkip) {
		HSSFRow row = startCell.getSheet().getRow(startCell.getRowIndex() + rowSkip);
		HSSFCell destinationCell = row.getCell(startCell.getColumnIndex() + columnSkip);
		return destinationCell;
	}

	private HSSFCell initCell(HSSFCell startCell, int rowSkip, int columnSkip) {
		HSSFRow row = startCell.getSheet().getRow(startCell.getRowIndex() + rowSkip);
		if(row == null)
			row = startCell.getSheet().createRow(startCell.getRowIndex() + rowSkip);
		HSSFCell destinationCell = row.getCell(startCell.getColumnIndex() + columnSkip);
		if(destinationCell == null)
			destinationCell = row.createCell(startCell.getColumnIndex() + columnSkip);
		return destinationCell;
	}

	private HSSFCell getDateCell(int dayToSeek, HSSFSheet monthSheet) {
		for (int i = monthSheet.getFirstRowNum(); i < monthSheet.getLastRowNum(); i++) {
			HSSFRow row = monthSheet.getRow(i);
			if(row == null)
				continue;
			HSSFCell cell = row.getCell(0);
			if(cell == null) continue;
			if(cell.getCellType() == HSSFCell.CELL_TYPE_STRING) continue;
			if(HSSFDateUtil.isCellDateFormatted(cell)){
				String cellFormula = cell.getCellFormula();
				String dayStr = cellFormula.split(",")[2].replace(")", "");
				int day = Integer.parseInt(dayStr);
				if(day == dayToSeek){
					return cell;
				}
			}
		}
		return null;
	}

	HSSFCellStyle dateCellStyle = null;
	CreationHelper createHelper = null;

	private void findDate(HSSFSheet sheet1) {
		for (Row row : sheet1) {
			Cell cell = row.getCell(0);
			if(cell != null)
			{
				if(cell.getCellType() != Cell.CELL_TYPE_STRING){
					if(HSSFDateUtil.isCellDateFormatted(cell)){
						logger.debug(row.getRowNum()+"/"+HSSFDateUtil.isCellDateFormatted(cell));
					}
				}
			}
		}
	}

	private void saveExcel(Workbook workbook, String fileName) {
		// Create a FileOutputStream by passing the excel file name.
		FileOutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(fileName);
			// Write the FileOutputStream to workbook object.
			workbook.write(outputStream);
			// Finally close the FileOutputStream.
			outputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void setRCIntegerValue(HSSFSheet sheet1, Integer rowIndex, int cellIndex, Integer value) {
		if(value != null){
			sheet1.getRow(rowIndex).getCell(cellIndex).setCellValue(value);
		}
	}

	private HSSFWorkbook readExcel() {
		try {
			InputStream inputStream = new FileInputStream(
					AppConfig.applicationExcelFolderPfad+"pyx2015.xls");
			return new HSSFWorkbook(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private Integer parseInt(Map<String, Object> map, String key) {
		Integer value = null;
		Object valueOnject = map.get(key);
		if(valueOnject instanceof Integer)
			value = (Integer) valueOnject;
		else
			if(valueOnject != null)
				value = Integer.parseInt(""+valueOnject);
		return value;
	}

	private Integer findFirstDepartmentRow(HSSFSheet sheet1) {
		for (Row row : sheet1) {
			Cell cell = row.getCell(0);
			if(cell != null && cell.getCellType() == Cell.CELL_TYPE_STRING){
				String stringCellValue = cell.getStringCellValue();
				if("Хірургія".equals(stringCellValue)){
					int rowNum = row.getRowNum();
					return rowNum;
				}
			}
		}
		return null;
	}

	private void testExcel(List<Map<String, Object>> moveTodayPatientsList) {
		Workbook workbook = new HSSFWorkbook();
		// Create two sheet by calling createSheet of workbook.
		Sheet sheet = workbook.createSheet("Місяць");
		int startRow = 4;
		for (int i = 0; i < moveTodayPatientsList.size(); i++) {
			Map<String, Object> map = moveTodayPatientsList.get(i);
			String departmentName = (String) map.get("DEPARTMENT_NAME");
			// Create a row and put some cells in it.
			Row row = sheet.createRow(i + startRow);
			Cell cell = row.createCell(0);
			cell.setCellValue(departmentName);
		}
		saveExcel(workbook,AppConfig.applicationExcelFolderPfad+"excel2.xls");
	}

}
