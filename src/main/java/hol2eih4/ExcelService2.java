package hol2eih4;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("excelService")
public class ExcelService2 {
	private static final Logger logger = LoggerFactory.getLogger(ExcelService2.class);
	
	public void createExcel(List<Map<String, Object>> moveTodayPatientsList, DateTime dateTime) {
		HSSFWorkbook pyx2015 = readExcel();
		
		logger.debug(dateTime.toString());
		Cell cellSumDay = getCellSumDay(pyx2015, dateTime);
		logger.debug(""+cellSumDay+"/"+cellSumDay.getRowIndex()
		+"/"+cellSumDay.getColumnIndex()
		+"/"+cellSumDay.getNumericCellValue()
		);
		int firstDepartmentRow = getFirstDepartmentRowFromCellSumDay(cellSumDay);
		logger.debug(""+firstDepartmentRow);
		Cell cell = cellSumDay.getSheet().getRow(firstDepartmentRow).getCell(0);
		logger.debug(""+cell+"/");

		
		
		makeBackup();
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
			Files.copy(new File(AppConfig.applicationExcelFolderPfad+"pyx2015.xls").toPath()
			, new File(AppConfig.innerExcelFolderPfad+"pyx2015.xls").toPath()
			, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private HSSFWorkbook readExcel() {
		try {
			InputStream inputStream = new FileInputStream(
					AppConfig.applicationExcelFolderPfad+"рух-2015-v.2.xls");
			return new HSSFWorkbook(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
