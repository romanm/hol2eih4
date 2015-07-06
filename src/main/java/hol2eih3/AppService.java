package hol2eih3;

import java.io.File;
import java.io.IOException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

import org.h2.Driver;
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
	public List<Map<String, Object>> readMoveTodayPatients() {
		logger.debug("readMoveTodayPatients");
		Date today = new Date();
		String readMoveTodayPatients_Sql = "select d.DEPARTMENT_ID, d.DEPARTMENT_NAME, m.MOVEDEPARTMENTPATIENT_ID , m.MOVEDEPARTMENTPATIENT_IN, m.MOVEDEPARTMENTPATIENT_IT,MOVEDEPARTMENTPATIENT_OUT "
				+ " from hol2.DEPARTMENT d left join "
				+ " (select * from hol2.MOVEDEPARTMENTPATIENT m where m.MOVEDEPARTMENTPATIENT_DATE = parsedatetime( ?,'dd-MM-yyyy')) m "
				+ " on d.DEPARTMENT_ID = m.DEPARTMENT_ID ";
		logger.debug(readMoveTodayPatients_Sql.replaceFirst("\\?", ddMMyyyDateFormat.format(today)));
		List<Map<String, Object>> moveTodayPatients = h2JdbcTemplate.queryForList(readMoveTodayPatients_Sql,ddMMyyyDateFormat.format(today));
		logger.debug(""+moveTodayPatients.size());
		return moveTodayPatients;
	}
	// 1.2   Запис надходження/виписки хворих на сьогодні – saveMoveTodayPatients
	public void saveMoveTodayPatients(Map<String, Object> moveTodayPatients) {
		logger.debug("saveMoveTodayPatients");
		Long today = (Long) moveTodayPatients.get("today");
		Date date = new Date(today);
		List<Map<String, Object>> moveTodayPatientsList = (List) moveTodayPatients.get("moveTodayPatientsList");
		for (Map<String, Object> map : moveTodayPatientsList) {
			Integer mOVEDEPARTMENTPATIENT_ID = (Integer) map.get("MOVEDEPARTMENTPATIENT_ID");
			if(mOVEDEPARTMENTPATIENT_ID == null){
				insertMoveDepartmentPatient(map,date);
			}else
				if(updateMoveDepartmentPatient(map) == 0){
					insertMoveDepartmentPatient(map,date);
			}
		}
	}
	private int updateMoveDepartmentPatient(Map<String, Object> map) {
		Integer mOVEDEPARTMENTPATIENT_IN = parseInt(map,"MOVEDEPARTMENTPATIENT_IN");
		Integer mOVEDEPARTMENTPATIENT_IT = parseInt(map,"MOVEDEPARTMENTPATIENT_IT");
		Integer mOVEDEPARTMENTPATIENT_OUT = parseInt(map,"MOVEDEPARTMENTPATIENT_OUT");
		Integer mOVEDEPARTMENTPATIENT_ID = (Integer) map.get("MOVEDEPARTMENTPATIENT_ID");
		final String sql = "UPDATE hol2.MOVEDEPARTMENTPATIENT "
				+ " SET MOVEDEPARTMENTPATIENT_IN = ?, MOVEDEPARTMENTPATIENT_IT = ?, MOVEDEPARTMENTPATIENT_OUT = ? WHERE MOVEDEPARTMENTPATIENT_ID = ?";
		int update = h2JdbcTemplate.update( sql, new Object[] {mOVEDEPARTMENTPATIENT_IN, mOVEDEPARTMENTPATIENT_IT, mOVEDEPARTMENTPATIENT_OUT
				, mOVEDEPARTMENTPATIENT_ID}
		, new int[] {Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER} );
		return update;
	}
	SimpleDateFormat ddMMyyyDateFormat = new SimpleDateFormat("dd-MM-yyyy");
	private void insertMoveDepartmentPatient(Map<String, Object> map, Date date) {
		Integer dEPARTMENT_ID = (Integer) map.get("DEPARTMENT_ID");
		//		String mOVEDEPARTMENTPATIENT_DATE = (String) map.get("MOVEDEPARTMENTPATIENT_DATE");
		String mOVEDEPARTMENTPATIENT_DATE = ddMMyyyDateFormat.format(date);
		Integer mOVEDEPARTMENTPATIENT_IN = parseInt(map,"MOVEDEPARTMENTPATIENT_IN");
		Integer mOVEDEPARTMENTPATIENT_IT = parseInt(map,"MOVEDEPARTMENTPATIENT_IT");
		Integer mOVEDEPARTMENTPATIENT_OUT = parseInt(map,"MOVEDEPARTMENTPATIENT_OUT");
		if(mOVEDEPARTMENTPATIENT_IN != null || mOVEDEPARTMENTPATIENT_IT != null || mOVEDEPARTMENTPATIENT_OUT != null){
			logger.debug(dEPARTMENT_ID+" " +mOVEDEPARTMENTPATIENT_DATE+" "+mOVEDEPARTMENTPATIENT_IN+" "+mOVEDEPARTMENTPATIENT_IT+" "+mOVEDEPARTMENTPATIENT_OUT);
			final String sql = "INSERT INTO hol2.MOVEDEPARTMENTPATIENT (DEPARTMENT_ID, MOVEDEPARTMENTPATIENT_DATE, MOVEDEPARTMENTPATIENT_IN"
					+ ", MOVEDEPARTMENTPATIENT_IT, MOVEDEPARTMENTPATIENT_OUT)"
					+ " VALUES (?, parsedatetime(?,'dd-MM-yyyy'), ?, ?, ?)";
			h2JdbcTemplate.update( sql, new Object[] {dEPARTMENT_ID, mOVEDEPARTMENTPATIENT_DATE
					, mOVEDEPARTMENTPATIENT_IN, mOVEDEPARTMENTPATIENT_IT, mOVEDEPARTMENTPATIENT_OUT });
		}
	}
	private Integer parseInt(Map<String, Object> map, String key) {
		Integer value = null;
		Object valueOnject = map.get(key);
		if(valueOnject instanceof Integer)
			value = (Integer) valueOnject;
		else
			if(valueOnject != null)
				value = Integer.parseInt((String) valueOnject);
		return value;
	}
	// 2  Показ кількості надходжень/виписки хворих за останні 7 днів – movePatients.html.

	//2.1  Зчитування руху хворих за останні 7 днів – readMovePatients
	public Map<String, Object> readMovePatients() {
		Calendar todayInstance = Calendar.getInstance();
		todayInstance.add(Calendar.DAY_OF_MONTH, -7);
		Date todayMinus7 = todayInstance.getTime();
		String readMovePatients_Sql = "select * from hol2.MOVEDEPARTMENTPATIENT m where m.MOVEDEPARTMENTPATIENT_DATE >= ?";
		List<Map<String, Object>> readMovePatients = h2JdbcTemplate.queryForList(readMovePatients_Sql,todayMinus7);
		Map<String, Object> dateDepartmentMap = convertDateDepartmentMap(readMovePatients);
		List<Map<String, Object>> departments = readDepartments();
		dateDepartmentMap.put("departments", departments);
		return dateDepartmentMap;
	}
	
	private List<Map<String, Object>> readDepartments() {
		String departments_Sql = "select * from hol2.DEPARTMENT";
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
		String sqlDbVersion = "select * from DBVERSION ORDER BY DBVERSION_ID DESC LIMIT 1";
		List<Map<String, Object>> dbVersion = h2JdbcTemplate.queryForList(sqlDbVersion);
		logger.debug(" "+dbVersion);
		int thisDbVersionId = dbVersion.size() == 0 ? 0:(int) dbVersion.get(0).get("DBVERSION_ID");
		logger.debug(" "+thisDbVersionId);
		for (Map map : sqlVersionUpdateList) {
			final Integer dbVersionId = (Integer) map.get("dbVersionId");
			if(dbVersionId > thisDbVersionId){
				logger.debug("update DB structure to version"+dbVersionId);
				boolean containsKey = map.containsKey("run_method");
				if(containsKey){
					String runMethod = (String) map.get("run_method");
					if("insertDepartmentFromConfig".equals(runMethod)){
						insertDepartmentFromConfig();
					}
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
				h2JdbcTemplate.update("INSERT INTO DBVERSION (DBVERSION_ID) VALUES (?)",dbVersionId);
			}
		}
		System.out.println("---------test------------");
//		Map<String, Object> readMovePatients = readMovePatients();
//		logger.debug(""+readMovePatients);
		System.out.println("----------test-----------");
	}
	private void insertDepartmentFromConfig() {
		String configJsonJs = AppConfig.applicationFolderPfad + AppConfig.jsonDbFiles +"config.json.js";
		Map<String, Object> configHol = readJsonDbFile2map(configJsonJs);
		List<Map<String, Object>> departments = (List<Map<String, Object>>) configHol.get("departments");
		for (Map<String, Object> department : departments) {
			insertDepartment(department);
		}
	}
	private void insertDepartment(Map<String, Object> department) {
		// TODO Auto-generated method stub
		
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
	

}
