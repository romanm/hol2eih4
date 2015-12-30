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

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("excelService")
public class ExcelService2 {
	private static final Logger logger = LoggerFactory.getLogger(ExcelService2.class);
	@Autowired private AppService appService;
	
	public void initExcel(HttpServletResponse response) throws IOException {
		logger.debug("--------initExcel-----------------------------------------------");
		String fileName = "pyx-2016-v.2.pattern.xls";
		String fileName2 = "pyx-2016-v.2.pattern2.xls";
		String filePath = AppConfig.applicationExcelFolderPfad+fileName;
		responseStrings(response, filePath);
		HSSFWorkbook pyxPattern = readExcel(filePath);
		for (String m : urkMonthNames) {
			if(m.length()<2)
				continue;
			HSSFSheet monthSheet = pyxPattern.getSheet(m);
			logger.debug(m+" "+monthSheet);
			responseStrings(response, ""+monthSheet);
			for (Row row : monthSheet) {
				Cell cell = row.getCell(22);
				if(cell != null && cell.getCellType() == Cell.CELL_TYPE_FORMULA){
					Double numericCellValue = cell.getNumericCellValue();
					int intValue = numericCellValue.intValue();
					int rowIndex = cell.getRowIndex();
					String string = "d "+intValue+"/"+rowIndex;
					logger.debug(string);
					responseStrings(response, string);
					int rowData = rowIndex-1;
					for (int r = 1; r <= 22; r++) {
						for (int c = 3; c <= 18; c++) {
							if(c==10) continue;
							HSSFRow row2 = monthSheet.getRow(rowIndex-r);
							HSSFCell cell2 = row2.getCell(c);
							cell2.setCellValue("");
//							row2.removeCell(cell2);
						}
					}
					
				}
			}
		}
		logger.debug(""+pyxPattern);
		saveExcel(pyxPattern, AppConfig.applicationExcelFolderPfad+fileName2);
	}
	private void responseStrings(HttpServletResponse response, String string) throws IOException {
		response.getOutputStream().write(string.getBytes());
		response.getOutputStream().write("\n".getBytes());
	}
	public void createExcel(DateTime dateTime) {
		logger.debug("--------createExcel-----------------------------------------------");
		HSSFWorkbook pyx2015 = readExcel(AppConfig.applicationExcelFolderPfad+AppConfig.excelFileName);
		int monthOfYear = dateTime.getMonthOfYear();
		List<Map<String, Object>> moveTodayPatientsList = appService.readMoveTodayPatients(dateTime);
		Cell cellSumDay = getCellSumDay(pyx2015, dateTime);
		setPatientMovesDate(moveTodayPatientsList, cellSumDay);

		DateTime minusDays = dateTime.minusDays(1);
		while (minusDays.getMonthOfYear() == monthOfYear) {
			Integer countDayPatient = appService.countDayPatient(minusDays);
			cellSumDay = getCellSumDay(pyx2015, minusDays);
			Cell cellSumDepartmentIn = cellSumDay.getSheet().getRow(cellSumDay.getRowIndex()).getCell(3);
			double numberValueSumDepartmentIn = cellSumDepartmentIn.getSheet().getWorkbook()
					.getCreationHelper().createFormulaEvaluator()
					.evaluate(cellSumDepartmentIn).getNumberValue();
			if(!(numberValueSumDepartmentIn > 0)&& countDayPatient > 0){
			}
				moveTodayPatientsList = appService.readMoveTodayPatients(minusDays);
				setPatientMovesDate(moveTodayPatientsList, cellSumDay);
			minusDays = minusDays.minusDays(1);
		}

		DateTime plusDays = dateTime.plusDays(1);
		while (plusDays.getMonthOfYear() == monthOfYear) {
			Integer countDayPatient = appService.countDayPatient(plusDays);
			cellSumDay = getCellSumDay(pyx2015, plusDays);
			Cell cellSumDepartmentIn = cellSumDay.getSheet().getRow(cellSumDay.getRowIndex()).getCell(3);
			double numberValueSumDepartmentIn = pyx2015.getCreationHelper().createFormulaEvaluator().evaluate(cellSumDepartmentIn).getNumberValue();
			if(!(numberValueSumDepartmentIn > 0) && countDayPatient > 0){
			}
				moveTodayPatientsList = appService.readMoveTodayPatients(plusDays);
				setPatientMovesDate(moveTodayPatientsList, cellSumDay);
			plusDays = plusDays.plusDays(1);
		}
		
		String fileName = AppConfig.applicationExcelFolderPfad+AppConfig.excelFileName;
		logger.debug("--------fileName-----------------------------------");
		logger.debug(fileName);
		saveExcel(pyx2015, fileName);
		makeBackup();
	}
	private int getFirstDepartmentRowFromCellSumDay(Cell cellSumDay) {
		int rowIndex = cellSumDay.getRowIndex();
		int firstDepartmentRow = rowIndex-departmentCount+1;
		return firstDepartmentRow;
	}
	private void setPatientMovesDate(List<Map<String, Object>> moveTodayPatientsList, Cell cellSumDay ) {
		Sheet sheet = cellSumDay.getSheet();
		logger.debug("--------------------------------------------");
		logger.debug(sheet.getSheetName());
		int dayOfMonth = (int) sheet.getWorkbook()
				.getCreationHelper().createFormulaEvaluator()
				.evaluate(cellSumDay).getNumberValue();
		CellReference cellReference = new CellReference(cellSumDay);
		logger.debug(""+cellSumDay.getRowIndex()+"/"+cellSumDay.getColumnIndex()+"/"+cellReference);
		logger.debug(""+dayOfMonth);
		int rowNr = getFirstDepartmentRowFromCellSumDay(cellSumDay) - 1;
		logger.debug("--------------------------------------------");
		for (Map<String, Object> map : moveTodayPatientsList) {
			if(dayOfMonth == 1){//first month day
				Cell setRCIntegerValue = setRCIntegerValue(sheet,rowNr,2,parseInt(map, "MOVEDEPARTMENTPATIENT_PATIENT1DAY"));
			}
			Integer parseInt = parseInt(map, "MOVEDEPARTMENTPATIENT_IN");
			setRCIntegerValue(sheet,rowNr,3,parseInt);
			setRCIntegerValue(sheet,rowNr,4,parseInt(map, "MOVEDEPARTMENTPATIENT_INDEPARTMENT"));
			setRCIntegerValue(sheet,rowNr,6,parseInt(map, "MOVEDEPARTMENTPATIENT_OUTDEPARTMENT"));
			setRCIntegerValue(sheet,rowNr,8,parseInt(map, "MOVEDEPARTMENTPATIENT_OUT"));
			setRCIntegerValue(sheet,rowNr,9,parseInt(map, "MOVEDEPARTMENTPATIENT_DEAD"));
			setRCIntegerValue(sheet,rowNr,14,parseInt(map, "MOVEDEPARTMENTPATIENT_SITY"));
			setRCIntegerValue(sheet,rowNr,15,parseInt(map, "MOVEDEPARTMENTPATIENT_LYING"));
			setRCIntegerValue(sheet,rowNr,16,parseInt(map, "MOVEDEPARTMENTPATIENT_CHILD"));
			setRCIntegerValue(sheet,rowNr,17,parseInt(map, "MOVEDEPARTMENTPATIENT_INSURED"));
			setRCIntegerValue(sheet,rowNr,18,parseInt(map, "MOVEDEPARTMENTPATIENT_CAES"));
			rowNr++;
		}
	}
	private Cell getCellSumDay(HSSFWorkbook pyx2015, DateTime dateTime) {
		int monthOfYear = dateTime.getMonthOfYear();
		int dayOfMonth = dateTime.getDayOfMonth();
		HSSFSheet monthSheet = pyx2015.getSheet(urkMonthNames[monthOfYear]);
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
	
	private void saveExcel(Workbook workbook, String fileName) {
		logger.debug(fileName);
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
	
	private Cell setRCIntegerValue(Sheet sheet1, Integer rownum, int cellnum, Integer value) {
//		if(value != null){
		if(true){
			Row row = sheet1.getRow(rownum);
			if(row == null)
				row = sheet1.createRow(rownum);
			Cell cell = row.getCell(cellnum);
			
			if(cell == null)
				cell = row.createCell(cellnum);
			if(cell.getCellType() == Cell.CELL_TYPE_FORMULA){
				cell.setCellType(Cell.CELL_TYPE_NUMERIC);
			}
			if(value == null){
				cell.setCellValue((String)null);
			}else{
				cell.setCellValue(value);
			}
			return cell;
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
	int departmentCount = 22;
	
	private static final String[] urkMonthNames = 
		{"" ,"січень" ,"лютий"
		,"березень" ,"квітень" ,"травень"
		,"червень" ,"липень" ,"серпень"
		,"вересень" ,"жовтень" ,"листопад"
		,"грудень" };
	
	private void makeBackup() {
		try {
			Files.copy(new File(AppConfig.applicationExcelFolderPfad+AppConfig.excelFileName).toPath()
			, new File(AppConfig.innerExcelFolderPfad+AppConfig.excelFileName).toPath()
			, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private HSSFWorkbook readExcel(String filePath) {
		try {
			
//			String filePath = AppConfig.applicationExcelFolderPfad+AppConfig.excelFileName;
			logger.debug("------------readExcel--------------------------");
			logger.debug(filePath);
			InputStream inputStream = new FileInputStream(
					filePath);
			return new HSSFWorkbook(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
