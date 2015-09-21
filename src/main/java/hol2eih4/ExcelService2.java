package hol2eih4;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("excelService")
public class ExcelService2 {
	private static final Logger logger = LoggerFactory.getLogger(ExcelService2.class);
	
	public void createExcel(List<Map<String, Object>> moveTodayPatientsList, DateTime dateTime) {
		HSSFWorkbook pyx2015 = readExcel();
		
		Cell cellSumDay = getCellSumDay(pyx2015, dateTime);
		int firstDepartmentRow = getFirstDepartmentRowFromCellSumDay(cellSumDay);
		setPatientMovesDate(moveTodayPatientsList, cellSumDay.getSheet(), firstDepartmentRow);
		saveExcel(pyx2015, AppConfig.applicationExcelFolderPfad+AppConfig.excelFileName);
		makeBackup();
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
	private void setPatientMovesDate(List<Map<String, Object>> moveTodayPatientsList, Sheet sheet, int rowNr) {
		for (Map<String, Object> map : moveTodayPatientsList) {
			setRCIntegerValue2(sheet,rowNr,3,parseInt(map, "MOVEDEPARTMENTPATIENT_IN"));
			setRCIntegerValue2(sheet,rowNr,4,parseInt(map, "MOVEDEPARTMENTPATIENT_INDEPARTMENT"));
			setRCIntegerValue2(sheet,rowNr,6,parseInt(map, "MOVEDEPARTMENTPATIENT_OUTDEPARTMENT"));
			setRCIntegerValue2(sheet,rowNr,8,parseInt(map, "MOVEDEPARTMENTPATIENT_OUT"));
			setRCIntegerValue2(sheet,rowNr,9,parseInt(map, "MOVEDEPARTMENTPATIENT_DEAD"));
			setRCIntegerValue2(sheet,rowNr,14,parseInt(map, "MOVEDEPARTMENTPATIENT_SITY"));
			setRCIntegerValue2(sheet,rowNr,15,parseInt(map, "MOVEDEPARTMENTPATIENT_LYING"));
			setRCIntegerValue2(sheet,rowNr,16,parseInt(map, "MOVEDEPARTMENTPATIENT_CHILD"));
			setRCIntegerValue2(sheet,rowNr,17,parseInt(map, "MOVEDEPARTMENTPATIENT_INSURED"));
			setRCIntegerValue2(sheet,rowNr,18,parseInt(map, "MOVEDEPARTMENTPATIENT_CAES"));

			rowNr++;
		}
	}
	private Cell setRCIntegerValue2(Sheet sheet1, Integer rownum, int cellnum, Integer value) {
		if(value != null){
			Row row = sheet1.getRow(rownum);
			if(row == null)
				row = sheet1.createRow(rownum);
			Cell cell = row.getCell(cellnum);
			if(cell == null)
				cell = row.createCell(cellnum);
			cell.setCellValue(value);
			return cell;
		}
		return null;
	}
	private Integer setPatientMovesDate2(List<Map<String, Object>> moveTodayPatientsList, HSSFSheet sheet, Integer rowNr) {
		for (Map<String, Object> map : moveTodayPatientsList) {
			setRCIntegerValue(sheet,rowNr,1,parseInt(map, "DEPARTMENT_BED"));
			setRCIntegerValue(sheet,rowNr,2,parseInt(map, "MOVEDEPARTMENTPATIENT_PATIENT1DAY"));
			setRCIntegerValue(sheet,rowNr,3,parseInt(map, "MOVEDEPARTMENTPATIENT_IN"));
			setRCIntegerValue(sheet,rowNr,4,parseInt(map, "MOVEDEPARTMENTPATIENT_INDEPARTMENT"));
			setRCIntegerValue(sheet,rowNr,6,parseInt(map, "MOVEDEPARTMENTPATIENT_OUTDEPARTMENT"));
			setRCIntegerValue(sheet,rowNr,8,parseInt(map, "MOVEDEPARTMENTPATIENT_OUT"));
			setRCIntegerValue(sheet,rowNr,9,parseInt(map, "MOVEDEPARTMENTPATIENT_DEAD"));
			setRCIntegerValue(sheet,rowNr,10,parseInt(map, "MOVEDEPARTMENTPATIENT_PATIENT2DAY"));
			setRCIntegerValue(sheet,rowNr,14,parseInt(map, "MOVEDEPARTMENTPATIENT_SITY"));
			setRCIntegerValue(sheet,rowNr,15,parseInt(map, "MOVEDEPARTMENTPATIENT_LYING"));
			setRCIntegerValue(sheet,rowNr,16,parseInt(map, "MOVEDEPARTMENTPATIENT_CHILD"));
			setRCIntegerValue(sheet,rowNr,17,parseInt(map, "MOVEDEPARTMENTPATIENT_INSURED"));
			setRCIntegerValue(sheet,rowNr,18,parseInt(map, "MOVEDEPARTMENTPATIENT_CAES"));
			rowNr++;
		}
		return rowNr;
	}
	
	private void setRCIntegerValue(HSSFSheet sheet1, Integer rownum, int cellnum, Integer value) {
		if(value != null){
			HSSFRow row = sheet1.getRow(rownum);
			if(row == null)
				row = sheet1.createRow(rownum);
			HSSFCell cell = row.getCell(cellnum);
			if(cell == null)
				cell = row.createCell(cellnum);
			cell.setCellValue(value);
		}
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
	int departmentCount = 22;
	private int getFirstDepartmentRowFromCellSumDay(Cell cellSumDay) {
		int rowIndex = cellSumDay.getRowIndex();
		int firstDepartmentRow = rowIndex-departmentCount+1;
		return firstDepartmentRow;
	}
	private static final String[] urkMonthNames = 
		{"" ,"січень" ,"лютий"
		,"березень" ,"квітень" ,"травень"
		,"червень" ,"липень" ,"серпень"
		,"вересень" ,"жовтень" ,"листопад"
		,"грудень" };
	private Cell getCellSumDay(HSSFWorkbook pyx2015, DateTime dateTime) {
		HSSFSheet monthSheet = pyx2015.getSheet(urkMonthNames[dateTime.getMonthOfYear()]);
		int dayOfMonth = dateTime.getDayOfMonth();
		Cell cellSumDay = null;
		for (Row row : monthSheet) {
			Cell cell = row.getCell(22);
			if(cell != null){
				if(cell.getCellType() == Cell.CELL_TYPE_FORMULA){
					Double numericCellValue = cell.getNumericCellValue();
					int intValue = numericCellValue.intValue();
					if(dayOfMonth == intValue){
						cellSumDay = cell;
						break;
					}
				}
			}
		}
		return cellSumDay;
	}
	private void makeBackup() {
		try {
			Files.copy(new File(AppConfig.applicationExcelFolderPfad+AppConfig.excelFileName).toPath()
			, new File(AppConfig.innerExcelFolderPfad+AppConfig.excelFileName).toPath()
			, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private HSSFWorkbook readExcel() {
		try {
			
			InputStream inputStream = new FileInputStream(
					AppConfig.applicationExcelFolderPfad+AppConfig.excelFileName);
			return new HSSFWorkbook(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
