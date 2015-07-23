package hol2eih4;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.h2.Driver;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component("appService")
public class AppService {
	private static final Logger logger = LoggerFactory.getLogger(AppService.class);
	private JdbcTemplate h2JdbcTemplate;
	
	//  1  Запис надходжень/виписки хворих за сьогодні – saveMovePatients.html.
	
	//  1.1  Зчитування надходження/виписки хворих на сьогодні – readTodayMovePatients
	public List<Map<String, Object>> readMoveTodayPatients(DateTime today) {
		logger.debug("readMoveTodayPatients");
		String readMoveTodayPatients_Sql = "SELECT d.department_name, d.department_bed, d.department_id "
				+ ", m.movedepartmentpatient_date, m.movedepartmentpatient_in , m.movedepartmentpatient_out "
				+ ", m.movedepartmentpatient_it , m.movedepartmentpatient_bed , m.movedepartmentpatient_patient1day "
				+ ", m.movedepartmentpatient_patient2day , m.movedepartmentpatient_dead , m.movedepartmentpatient_indepartment "
				+ ", m.movedepartmentpatient_outdepartment, m.movedepartmentpatient_sity , m.movedepartmentpatient_child "
				+ ", m.movedepartmentpatient_lying, m.movedepartmentpatient_insured, m.movedepartmentpatient_caes, m.movedepartmentpatient_id "
				+ " FROM hol2.department d LEFT JOIN "
				+ " (SELECT * FROM hol2.movedepartmentpatient m WHERE m.movedepartmentpatient_date = PARSEDATETIME( ?,'dd-MM-yyyy')) m "
				+ " ON d.department_id = m.department_id "
				+ " WHERE d.department_sort IS NOT NULL "
				+ " ORDER BY department_sort";
		logger.debug(readMoveTodayPatients_Sql.replaceFirst("\\?", AppConfig.ddMMyyyDateFormat.format(today.toDate())));
		List<Map<String, Object>> moveTodayPatients = h2JdbcTemplate.queryForList(readMoveTodayPatients_Sql,AppConfig.ddMMyyyDateFormat.format(today.toDate()));
		logger.debug(""+moveTodayPatients.size());
		logger.debug(""+moveTodayPatients.get(0));
		return moveTodayPatients;
	}
	// 1.2   Запис надходження/виписки хворих на сьогодні – saveMoveTodayPatients
	public void saveMoveTodayPatients(Map<String, Object> moveTodayPatients, DateTime dateTime) {
		logger.debug("saveMoveTodayPatients "+dateTime);
		List<Map<String, Object>> moveTodayPatientsList = (List) moveTodayPatients.get("moveTodayPatientsList");
		for (Map<String, Object> map : moveTodayPatientsList) {
			Integer mOVEDEPARTMENTPATIENT_ID = (Integer) map.get("MOVEDEPARTMENTPATIENT_ID");
			if(mOVEDEPARTMENTPATIENT_ID == null){
				insertMoveDepartmentPatient(map,dateTime);
			}else
				if(updateMoveDepartmentPatient(map) == 0){
					insertMoveDepartmentPatient(map,dateTime);
			}
		}
	}
	private int updateMoveDepartmentPatient(Map<String, Object> map) {
		logger.debug("updateMoveDepartmentPatient");
		Integer mOVEDEPARTMENTPATIENT_IT = parseInt(map,"MOVEDEPARTMENTPATIENT_IT");
		Integer mOVEDEPARTMENTPATIENT_BED = parseInt(map,"MOVEDEPARTMENTPATIENT_BED");
		Integer mOVEDEPARTMENTPATIENT_PATIENT1DAY = parseInt(map,"MOVEDEPARTMENTPATIENT_PATIENT1DAY");
		
		Integer mOVEDEPARTMENTPATIENT_PATIENT2DAY = parseInt(map,"MOVEDEPARTMENTPATIENT_PATIENT2DAY");
		Integer mOVEDEPARTMENTPATIENT_DEAD = parseInt(map,"MOVEDEPARTMENTPATIENT_DEAD");
		Integer mOVEDEPARTMENTPATIENT_INDEPARTMENT = parseInt(map,"MOVEDEPARTMENTPATIENT_INDEPARTMENT");
		
		Integer mOVEDEPARTMENTPATIENT_OUTDEPARTMENT = parseInt(map,"MOVEDEPARTMENTPATIENT_OUTDEPARTMENT");
		Integer mOVEDEPARTMENTPATIENT_SITY = parseInt(map,"MOVEDEPARTMENTPATIENT_SITY");
		Integer mOVEDEPARTMENTPATIENT_CHILD = parseInt(map,"MOVEDEPARTMENTPATIENT_CHILD");
		
		Integer mOVEDEPARTMENTPATIENT_LYING = parseInt(map,"MOVEDEPARTMENTPATIENT_LYING");
		Integer mOVEDEPARTMENTPATIENT_INSURED = parseInt(map,"MOVEDEPARTMENTPATIENT_INSURED");
		
		Integer mOVEDEPARTMENTPATIENT_IN = parseInt(map,"MOVEDEPARTMENTPATIENT_IN");
		Integer mOVEDEPARTMENTPATIENT_OUT = parseInt(map,"MOVEDEPARTMENTPATIENT_OUT");
		Integer mOVEDEPARTMENTPATIENT_CAES = parseInt(map,"MOVEDEPARTMENTPATIENT_CAES");
		
		Integer mOVEDEPARTMENTPATIENT_ID = (Integer) map.get("MOVEDEPARTMENTPATIENT_ID");
		final String sql = "UPDATE hol2.movedepartmentpatient "
				+ " SET "
				+ " MOVEDEPARTMENTPATIENT_IT  = ?, MOVEDEPARTMENTPATIENT_BED  = ?, MOVEDEPARTMENTPATIENT_PATIENT1DAY = ?  "
				+ ", MOVEDEPARTMENTPATIENT_PATIENT2DAY  = ?, MOVEDEPARTMENTPATIENT_DEAD  = ?, MOVEDEPARTMENTPATIENT_INDEPARTMENT = ?  "
				+ ", MOVEDEPARTMENTPATIENT_OUTDEPARTMENT = ?, MOVEDEPARTMENTPATIENT_SITY  = ?, MOVEDEPARTMENTPATIENT_CHILD = ?  "
				+ ", MOVEDEPARTMENTPATIENT_LYING = ?, MOVEDEPARTMENTPATIENT_INSURED  = ? "
				+ ", MOVEDEPARTMENTPATIENT_IN  = ?, MOVEDEPARTMENTPATIENT_OUT = ? , MOVEDEPARTMENTPATIENT_CAES = ?  "
				+ " WHERE movedepartmentpatient_id = ?";
logger.debug(sql);
		int update = h2JdbcTemplate.update( sql, new Object[] {
				mOVEDEPARTMENTPATIENT_IT, mOVEDEPARTMENTPATIENT_BED, mOVEDEPARTMENTPATIENT_PATIENT1DAY
				,mOVEDEPARTMENTPATIENT_PATIENT2DAY, mOVEDEPARTMENTPATIENT_DEAD, mOVEDEPARTMENTPATIENT_INDEPARTMENT
				,mOVEDEPARTMENTPATIENT_OUTDEPARTMENT, mOVEDEPARTMENTPATIENT_SITY, mOVEDEPARTMENTPATIENT_CHILD
				,mOVEDEPARTMENTPATIENT_LYING, mOVEDEPARTMENTPATIENT_INSURED
				,mOVEDEPARTMENTPATIENT_IN, mOVEDEPARTMENTPATIENT_OUT, mOVEDEPARTMENTPATIENT_CAES
				, mOVEDEPARTMENTPATIENT_ID}
		, new int[] {
				Types.INTEGER, Types.INTEGER, Types.INTEGER
				,Types.INTEGER, Types.INTEGER, Types.INTEGER
				,Types.INTEGER, Types.INTEGER, Types.INTEGER
				,Types.INTEGER, Types.INTEGER
				,Types.INTEGER, Types.INTEGER, Types.INTEGER
				, Types.INTEGER
				} );
		return update;
	}
	
	private void insertMoveDepartmentPatient(Map<String, Object> map, DateTime dateTime) {
		logger.debug("insertMoveDepartmentPatient");
		Integer dEPARTMENT_ID = (Integer) map.get("DEPARTMENT_ID");
		//		String mOVEDEPARTMENTPATIENT_DATE = (String) map.get("MOVEDEPARTMENTPATIENT_DATE");
		String mOVEDEPARTMENTPATIENT_DATE = AppConfig.ddMMyyyDateFormat.format(dateTime.toDate());
		
		Integer mOVEDEPARTMENTPATIENT_IT = parseInt(map,"MOVEDEPARTMENTPATIENT_IT");
		Integer mOVEDEPARTMENTPATIENT_BED = parseInt(map,"MOVEDEPARTMENTPATIENT_BED");
		Integer mOVEDEPARTMENTPATIENT_PATIENT1DAY = parseInt(map,"MOVEDEPARTMENTPATIENT_PATIENT1DAY");
		
		Integer mOVEDEPARTMENTPATIENT_PATIENT2DAY = parseInt(map,"MOVEDEPARTMENTPATIENT_PATIENT2DAY");
		Integer mOVEDEPARTMENTPATIENT_DEAD = parseInt(map,"MOVEDEPARTMENTPATIENT_DEAD");
		Integer mOVEDEPARTMENTPATIENT_INDEPARTMENT = parseInt(map,"MOVEDEPARTMENTPATIENT_INDEPARTMENT");
		
		Integer mOVEDEPARTMENTPATIENT_OUTDEPARTMENT = parseInt(map,"MOVEDEPARTMENTPATIENT_OUTDEPARTMENT");
		Integer mOVEDEPARTMENTPATIENT_SITY = parseInt(map,"MOVEDEPARTMENTPATIENT_SITY");
		Integer mOVEDEPARTMENTPATIENT_CHILD = parseInt(map,"MOVEDEPARTMENTPATIENT_CHILD");
		
		Integer mOVEDEPARTMENTPATIENT_LYING = parseInt(map,"MOVEDEPARTMENTPATIENT_LYING");
		Integer mOVEDEPARTMENTPATIENT_INSURED = parseInt(map,"MOVEDEPARTMENTPATIENT_INSURED");
		
		Integer mOVEDEPARTMENTPATIENT_IN = parseInt(map,"MOVEDEPARTMENTPATIENT_IN");
		Integer mOVEDEPARTMENTPATIENT_OUT = parseInt(map,"MOVEDEPARTMENTPATIENT_OUT");
		Integer mOVEDEPARTMENTPATIENT_CAES = parseInt(map,"MOVEDEPARTMENTPATIENT_CAES");
		
		final String sql = "INSERT INTO hol2.movedepartmentpatient ("
				+ "department_id, movedepartmentpatient_date, MOVEDEPARTMENTPATIENT_BED"
				+ ", MOVEDEPARTMENTPATIENT_IT, MOVEDEPARTMENTPATIENT_PATIENT1DAY "
				+ ", MOVEDEPARTMENTPATIENT_PATIENT2DAY, MOVEDEPARTMENTPATIENT_DEAD, MOVEDEPARTMENTPATIENT_INDEPARTMENT "
				+ ", MOVEDEPARTMENTPATIENT_OUTDEPARTMENT, MOVEDEPARTMENTPATIENT_SITY, MOVEDEPARTMENTPATIENT_CHILD "
				+ ", MOVEDEPARTMENTPATIENT_LYING, MOVEDEPARTMENTPATIENT_INSURED"
				+ ", MOVEDEPARTMENTPATIENT_IN, MOVEDEPARTMENTPATIENT_OUT, MOVEDEPARTMENTPATIENT_CAES, MOVEDEPARTMENTPATIENT_ID"
				+ ") VALUES ("
				+ " ?, PARSEDATETIME(?,'dd-MM-yyyy'), ?"
				+ ", ?, ?"
				+ ", ?, ?, ?"
				+ ", ?, ?, ?"
				+ ", ?, ?"
				+ ", ?, ?, ?, ?"
				+ ")";
		Integer mOVEDEPARTMENTPATIENT_ID_test = 0;
		logger.debug(sql
				.replaceFirst("\\?", ""+dEPARTMENT_ID)
				.replaceFirst("\\?", ""+mOVEDEPARTMENTPATIENT_DATE)
				.replaceFirst("\\?", ""+mOVEDEPARTMENTPATIENT_BED)
				.replaceFirst("\\?", ""+mOVEDEPARTMENTPATIENT_IT)
				.replaceFirst("\\?", ""+mOVEDEPARTMENTPATIENT_PATIENT1DAY)
				.replaceFirst("\\?", ""+mOVEDEPARTMENTPATIENT_PATIENT2DAY)
				.replaceFirst("\\?", ""+mOVEDEPARTMENTPATIENT_DEAD)
				.replaceFirst("\\?", ""+mOVEDEPARTMENTPATIENT_INDEPARTMENT)
				.replaceFirst("\\?", ""+mOVEDEPARTMENTPATIENT_OUTDEPARTMENT)
				.replaceFirst("\\?", ""+mOVEDEPARTMENTPATIENT_SITY)
				.replaceFirst("\\?", ""+mOVEDEPARTMENTPATIENT_CHILD)
				.replaceFirst("\\?", ""+mOVEDEPARTMENTPATIENT_LYING)
				.replaceFirst("\\?", ""+mOVEDEPARTMENTPATIENT_INSURED)
				.replaceFirst("\\?", ""+mOVEDEPARTMENTPATIENT_IN)
				.replaceFirst("\\?", ""+mOVEDEPARTMENTPATIENT_OUT)
				.replaceFirst("\\?", ""+mOVEDEPARTMENTPATIENT_CAES)
				.replaceFirst("\\?", ""+mOVEDEPARTMENTPATIENT_ID_test)
				);
		logger.debug(dEPARTMENT_ID+" " +mOVEDEPARTMENTPATIENT_DATE+" "+mOVEDEPARTMENTPATIENT_IN+" "+mOVEDEPARTMENTPATIENT_IT+" "+mOVEDEPARTMENTPATIENT_OUT);
		if(mOVEDEPARTMENTPATIENT_IT != null || mOVEDEPARTMENTPATIENT_BED != null ||mOVEDEPARTMENTPATIENT_PATIENT1DAY 
				!= null || mOVEDEPARTMENTPATIENT_PATIENT2DAY != null || mOVEDEPARTMENTPATIENT_DEAD 
				!= null || mOVEDEPARTMENTPATIENT_INDEPARTMENT 
				!= null || mOVEDEPARTMENTPATIENT_OUTDEPARTMENT != null || mOVEDEPARTMENTPATIENT_SITY != null || mOVEDEPARTMENTPATIENT_CHILD 
				!= null || mOVEDEPARTMENTPATIENT_LYING != null || mOVEDEPARTMENTPATIENT_INSURED
				!= null || mOVEDEPARTMENTPATIENT_IN != null || mOVEDEPARTMENTPATIENT_OUT != null){
			Integer mOVEDEPARTMENTPATIENT_ID = nextDbId();
			map.put("MOVEDEPARTMENTPATIENT_ID", mOVEDEPARTMENTPATIENT_ID);
			h2JdbcTemplate.update( sql, new Object[] {dEPARTMENT_ID, mOVEDEPARTMENTPATIENT_DATE, mOVEDEPARTMENTPATIENT_BED
					, mOVEDEPARTMENTPATIENT_IT, mOVEDEPARTMENTPATIENT_PATIENT1DAY 
					, mOVEDEPARTMENTPATIENT_PATIENT2DAY, mOVEDEPARTMENTPATIENT_DEAD, mOVEDEPARTMENTPATIENT_INDEPARTMENT 
					, mOVEDEPARTMENTPATIENT_OUTDEPARTMENT, mOVEDEPARTMENTPATIENT_SITY, mOVEDEPARTMENTPATIENT_CHILD 
					, mOVEDEPARTMENTPATIENT_LYING, mOVEDEPARTMENTPATIENT_INSURED
					, mOVEDEPARTMENTPATIENT_IN, mOVEDEPARTMENTPATIENT_OUT, mOVEDEPARTMENTPATIENT_CAES, mOVEDEPARTMENTPATIENT_ID
			});
		}
	}
	public Integer nextDbId() {
		return h2JdbcTemplate.queryForObject("select nextval('dbid')", Integer.class);
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
	// 2  Показ кількості надходжень/виписки хворих за останні 7 днів – movePatients.html.

	//2.1  Зчитування руху хворих за останні 7 днів – readMovePatients
	public Map<String, Object> readMovePatients() {
		DateTime todayMinus7 = new DateTime().minusDays(7);
		String readMovePatients_Sql = "SELECT * FROM hol2.movedepartmentpatient m WHERE m.movedepartmentpatient_date >= ?";
		List<Map<String, Object>> readMovePatients = h2JdbcTemplate.queryForList(readMovePatients_Sql,todayMinus7.toDate());
		Map<String, Object> dateDepartmentMap = convertDateDepartmentMap(readMovePatients);
		List<Map<String, Object>> departments = readDepartments();
		dateDepartmentMap.put("departments", departments);
		return dateDepartmentMap;
	}
	
	private List<Map<String, Object>> readDepartments() {
		String departments_Sql = "SELECT * FROM hol1.department";
		List<Map<String, Object>> departments = h2JdbcTemplate.queryForList(departments_Sql);
		return departments;
	}

	private Map<String, Object> convertDateDepartmentMap(List<Map<String, Object>> readMovePatients) {
		Map<String, Object> dateDepartmentMap = new HashMap<String, Object>();
		for (Map<String, Object> map : readMovePatients) {
			String mDate = ""+map.get("MOVEDEPARTMENTPATIENT_DATE");
			if(!dateDepartmentMap.containsKey(mDate))
				dateDepartmentMap.put(mDate, new HashMap<String, Object>());
			Map<String, Object> departmentMap = (Map<String, Object>) dateDepartmentMap.get(mDate);
			String departmentId = ""+map.get("DEPARTMENT_ID");
			departmentMap.put(departmentId, map);
		}
		return dateDepartmentMap;
	}
	// 3  План операцій – operationsPlan.html

	// 3.1  Запис плану операцій на завтра – saveTomorrowOperationsPlan
	public void saveTomorrowOperationsPlan(Map<String, Object> tomorrowOperationsPlan) {
	}

	// 3.2  Зчитування плану операцій на сьогодні – readTodayOperationsPlan
	public Map<String, Object> readTodayOperationsPlan() {
		return null;
	}
	// 4  Список операцій відділення по датах –  departmentOperationsList.html
	
	// 4.1  Зчитування списку операцій за останні 7 днів –  readListOperationsPlan
	
	public Map<String, Object> readListOperationsPlan() {
		return null;
	}

	public AppService() throws NamingException{
		initH2();
	}
	
	private void initH2() {
		SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
		dataSource.setDriverClass(Driver.class);
		dataSource.setUrl(AppConfig.urlDb);
		dataSource.setUsername("sa");
//		dataSource.setPassword("");
		this.h2JdbcTemplate = new JdbcTemplate(dataSource);
		updateDbVersion();
	}

	private void updateDbVersion() {
		final Map<String, Object> dbVersionUpdate = readJsonDbFile2map(fileNameDbVersionUpdate);
		final List<Map> sqlVersionUpdateList = (List) dbVersionUpdate.get("dbVersionUpdateList");
		final List<String> sqls0 = (List<String>) ((Map) sqlVersionUpdateList.get(0)).get("sqls");
		for (String sql : sqls0) 
			h2JdbcTemplate.update(sql);
		String sqlDbVersion = "SELECT * FROM dbversion ORDER BY dbversion_id DESC LIMIT 1";
		List<Map<String, Object>> dbVersion = h2JdbcTemplate.queryForList(sqlDbVersion);
		logger.debug(" "+dbVersion);
		int thisDbVersionId = dbVersion.size() == 0 ? 0:(int) dbVersion.get(0).get("DBVERSION_ID");
		logger.debug(" "+thisDbVersionId);
		for (Map map : sqlVersionUpdateList) {
			final Integer dbVersionId = (Integer) map.get("dbVersionId");
			if(dbVersionId > thisDbVersionId){
				logger.debug("UPDATE DB structure to version"+dbVersionId);
				boolean containsKey = map.containsKey("run_method");
				if(containsKey){
					String runMethod = (String) map.get("run_method");
				}else{
					final List<String> sqls = (List<String>) map.get("sqls");
					for (String sql : sqls) {
						if(sql.indexOf("sql_update")>0){
							System.out.println(sql);
							List<Map<String, Object>> sqlUpdateList = h2JdbcTemplate.queryForList(sql);
							for (Map<String, Object> sqlToUpdateMap : sqlUpdateList) {
								String sqlToUpdate = (String) sqlToUpdateMap.get("sql_update");
								System.out.println(sqlToUpdate);
								h2JdbcTemplate.update(sqlToUpdate);
							}
						}else{
							logger.debug(sql);
							h2JdbcTemplate.update(sql);
						}
					}
				}
				h2JdbcTemplate.update("INSERT INTO dbversion (dbversion_id) VALUES (?)",dbVersionId);
			}
		}
		System.out.println("---------test------------");
//		Map<String, Object> readMovePatients = readMovePatients();
//		logger.debug(""+readMovePatients);
		System.out.println("----------test-----------");
	}

	final String fileNameDbVersionUpdate = AppConfig.applicationResourcesFolderPfad + "dbVersionUpdate.sql.json.js";
	private Map<String, Object> readJsonDbFile2map(String fileNameJsonDb) {
		logger.debug(fileNameJsonDb);
		File file = new File(fileNameJsonDb);
		logger.debug(" o - "+file);
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> readJsonDbFile2map = null;// = new HashMap<String, Object>();
		try {
			readJsonDbFile2map = mapper.readValue(file, Map.class);
//			logger.debug(" o - "+readJsonDbFile2map);
		} catch (JsonParseException e1) {
			e1.printStackTrace();
		} catch (JsonMappingException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
//		logger.debug(" o - "+readJsonDbFile2map);
		return readJsonDbFile2map;
	}

	public void createExcel(List<Map<String, Object>> moveTodayPatientsList) {
//		testExcel(moveTodayPatientsList);
		HSSFWorkbook pyx2015 = readExcel();
		logger.debug(""+pyx2015.getNumberOfSheets());
		logger.debug(""+pyx2015.getSheetName(0));
		HSSFSheet sheet1 = pyx2015.getSheetAt(0);
		Integer rowNr = findFirstDepartmentRow(sheet1);
		logger.debug(""+rowNr);
		String stringCellValue = sheet1.getRow(rowNr).getCell(0).getStringCellValue();
		logger.debug(""+stringCellValue);
		for (Map<String, Object> map : moveTodayPatientsList) {
			setRCIntegerValue(sheet1,rowNr,1,parseInt(map, "DEPARTMENT_BED"));
			Integer parseInt = parseInt(map, "MOVEDEPARTMENTPATIENT_PATIENT1DAY");
			logger.debug(""+parseInt);
			setRCIntegerValue(sheet1,rowNr,2,parseInt);
			rowNr++;
		}
		saveExcel(pyx2015,AppConfig.applicationExcelFolderPfad+"pyx2015.xls");
	}

	private void setRCIntegerValue(HSSFSheet sheet1, Integer rowIndex, int cellIndex, Integer value) {
		if(value != null){
			sheet1.getRow(rowIndex).getCell(cellIndex).setCellValue(value);
		}
	}

	private Integer findFirstDepartmentRow(HSSFSheet sheet1) {
		for (Row row : sheet1) {
			Cell cell = row.getCell(0);
			if(cell != null){
				String stringCellValue = cell.getStringCellValue();
				if("Хірургія".equals(stringCellValue)){
					int rowNum = row.getRowNum();
					return rowNum;
				}
			}
		}
		return null;
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
