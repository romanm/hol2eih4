package hol2eih4;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ReportRestService {
	private static final Logger logger = LoggerFactory.getLogger(ReportRestService.class);

	@Autowired private NamedParameterJdbcTemplate hol1EihParamJdbcTemplate;
	@Autowired private JdbcTemplate hol1EihJdbcTemplate;
	@Autowired private PropertyHolder propertyHolder;
	
	private @Value("${sql.hol1Eih.f20t3500.list}") String sqlHol1EihF20t3500List;
	
	@GetMapping("/r/F20t3500NrrPatienten-{m1}-{m2}-{year}-{nrr}")
	public  @ResponseBody Map<String, Object> readF20t3500NrrPatienten(
			@PathVariable Integer m1
			,@PathVariable Integer m2
			,@PathVariable Integer year
			,@PathVariable Integer nrr
			,Principal userPrincipal) {
		
		StopWatch watch = new StopWatch();
		watch.start();
		Map<String, Object> map = new HashMap<>();
		map.put("min_month", m1);
		map.put("max_month", m2);
		map.put("year", year);
		logger.info(" ------------------------- \n"
				+ "/r/F20t3500NrrPatienten-{m1}-{m2}-{year}-{nrr}"+nrr + map);
		int operationSubgroupId = nrr%100;
		String sqlListOfPatient;
		if(operationSubgroupId > 0){
			//читити підгрупу операцій
			sqlListOfPatient = sqlHol1EihF20t3500List + " AND operation_subgroup_id = " + operationSubgroupId;
		}else{
			//читити групу операцій
			sqlListOfPatient = sqlHol1EihF20t3500List + " AND operation_group_id = " + nrr/100;
		}
		logger.info(" ------------------------- \n"
				+ sqlListOfPatient
				+ "\n"
				+ operationSubgroupId);
		List<Map<String, Object>> listOfPatient
		= hol1EihParamJdbcTemplate.queryForList(sqlListOfPatient,map);
		map.put("listOfPatient", listOfPatient);
		Map<String, Object> map2 = readF20t3500(m1,m2,year,userPrincipal);
		map.put("list", map2.get("list"));
		
		watch.stop();
		map.put("duration", watch.getTotalTimeSeconds());
		System.out.println("duration = " + map.get("duration"));
		return map;
	}


	@GetMapping("/r/F20t3100NrrPatienten-{m1}-{m2}-{year}-{nrr}")
	public  @ResponseBody Map<String, Object> readF20t3100NrrPatienten(
			@PathVariable Integer m1
			,@PathVariable Integer m2
			,@PathVariable Integer year
			,@PathVariable Integer nrr
			,Principal userPrincipal) {
		
		StopWatch watch = new StopWatch();
		watch.start();
		Map<String, Object> map = new HashMap<>();
		map.put("min_month", m1);
		map.put("max_month", m2);
		map.put("year", year);
		logger.info(" ------------------------- \n"
				+ "/r/F20t3500NrrPatienten-{m1}-{m2}-{year}-{nrr}"+nrr + map);

		watch.stop();
		map.put("duration", watch.getTotalTimeSeconds());
		System.out.println("duration = " + map.get("duration"));
		return map;
	}

	@GetMapping("/r/F20t3600NrrPatienten-{m1}-{m2}-{year}-{nrr}")
	public  @ResponseBody Map<String, Object> readF20t3600NrrPatienten(
			@PathVariable Integer m1
			,@PathVariable Integer m2
			,@PathVariable Integer year
			,@PathVariable String nrr
			,Principal userPrincipal) {
		StopWatch watch = new StopWatch();
		watch.start();
		Map<String, Object> map = new HashMap<>();
		map.put("min_month", m1);
		map.put("max_month", m2);
		map.put("year", year);
		logger.info("\n -------------------------  /r/F20t3600NrrPatienten- "+nrr + map);
		String nrr_sql = propertyHolder.get("sql.hol1Eih.f20t3600.row." + nrr);
		logger.info("\n "+nrr_sql);
		String sql = sqlHol1EihF20t3600NrrPatientsYearMonth.replace(":nrr_sql", nrr_sql);
		logger.info("\n "+sql.replace(":min_month", ""+m1).replace(":max_month", ""+m2).replace(":year", ""+year));
		List<Map<String, Object>> list = hol1EihParamJdbcTemplate.queryForList(sql,map);
		map.put("nrr", nrr);
		map.put("list", list);
		watch.stop();
		map.put("duration", watch.getTotalTimeSeconds());
		System.out.println("duration = " + map.get("duration"));
		return map;
	}

	private @Value("${sql.hol1Eih.f20t3600.nrr_patients.year_month}") String sqlHol1EihF20t3600NrrPatientsYearMonth;
	private @Value("${sql.hol1Eih.f20t3220.nrr_patients.year_month}") String sqlHol1EihF20t3220NrrPatientsYearMonth;

	@GetMapping("/r/F20t3220NrrPatienten-{m1}-{m2}-{year}-{nrr}")
	public  @ResponseBody Map<String, Object> readF20t3220NrrPatienten(
			@PathVariable Integer m1
			,@PathVariable Integer m2
			,@PathVariable Integer year
			,@PathVariable String nrr
			,Principal userPrincipal) {
		StopWatch watch = new StopWatch();
		watch.start();
		Map<String, Object> map = new HashMap<>();
		map.put("min_month", m1);
		map.put("max_month", m2);
		map.put("year", year);
		logger.info("\n -------------------------  /r/F20t3220NrrPatienten- "+nrr + map);
		String nrr_sql = propertyHolder.get("sql.hol1Eih.f20t3220.history_diagnos-icd." + nrr);
		logger.info("\n "+nrr_sql);
		String sql = sqlHol1EihF20t3220NrrPatientsYearMonth.replace(":nrr_sql", nrr_sql);
		logger.info("\n "
		+sql
		.replaceAll(":min_month", map.get("min_month").toString())
		.replaceAll(":max_month", map.get("max_month").toString())
		.replaceAll(":year", map.get("year").toString())
		);
		List<Map<String, Object>> list 
			= hol1EihParamJdbcTemplate.queryForList(sql,map);
		logger.info("\n -------------------------  /r/F20t3220NrrPatienten- "+nrr + map);
		map.put("nrr", nrr);
		map.put("list", list);
		watch.stop();
		map.put("duration", watch.getTotalTimeSeconds());
		System.out.println("duration = " + map.get("duration"));
		return map;
	}

	private @Value("${sql.hol1Eih.f20t3220.year_month2}") String sqlHol1EihF20t3220YearMonth2;
	@RequestMapping(value = "/r/2_F20t3220-{m1}-{m2}-{year}", method = RequestMethod.GET)
	public  @ResponseBody Map<String, Object> read2_F20t3220(
			@PathVariable Integer m1
			,@PathVariable Integer m2
			,@PathVariable Integer year
			,Principal userPrincipal) {
		logger.info("\n -------------------------  /r/2_F20t3220");
		return readSQL_FOR_F20t3220(m1, m2, year, sqlHol1EihF20t3220YearMonth2);
	}

	private @Value("${sql.hol1Eih.f20t3220.year_month}") String sqlHol1EihF20t3220YearMonth;
	@GetMapping("/r/F20t3220-{m1}-{m2}-{year}")
	public  @ResponseBody Map<String, Object> readF20t3220(
			@PathVariable Integer m1
			,@PathVariable Integer m2
			,@PathVariable Integer year
			,Principal userPrincipal) {
		logger.info(" ------------------------- \n"
				+ "/r/F20t3220-{m1}-{m2}-{year} " 
				+ sqlHol1EihF20t3220YearMonth
		.replaceAll(":min_month", m1.toString())
		.replaceAll(":max_month", m2.toString())
		.replaceAll(":year", year.toString())
				);
		return readSQL_FOR_F20t3220(m1, m2, year, sqlHol1EihF20t3220YearMonth);
	}

	private Map<String, Object> readSQL_FOR_F20t3220(Integer m1, Integer m2, Integer year,
			String sqlHol1Eih_TO_F20t3220YearMonth) {
		StopWatch watch = new StopWatch();
		watch.start();
		Map<String, Object> map = new HashMap<>();
		map.put("min_month", m1);
		map.put("max_month", m2);
		map.put("year", year);
		logger.info("\n -------------------------  /r/F20t3220- " + map + ""
				+ "\n" + sqlHol1Eih_TO_F20t3220YearMonth
				.replaceAll(":min_month", map.get("min_month").toString())
				.replaceAll(":max_month", map.get("max_month").toString())
				.replaceAll(":year", map.get("year").toString())
				);
		List<Map<String, Object>> queryForList = hol1EihParamJdbcTemplate.queryForList(sqlHol1Eih_TO_F20t3220YearMonth,map);
		map.put("list", queryForList);
		map.put("m1", m1);
		map.put("m2", m2);
		watch.stop();
		map.put("duration", watch.getTotalTimeSeconds());
		System.out.println("duration = " + map.get("duration"));
		return map;
	}

	private @Value("${sql.hol1Eih.f20t3500.nrr_group}") String sqlHol1EihF20t3500NrrGroup;
	private @Value("${sql.hol1Eih.f20t3500.all}") String sqlHol1EihF20t3500All;
	private @Value("${sql.hol1Eih.operationHistory.countOfMonthYearPerion}")
		String sqlHol1EihOperationHistoryCountOfMonthYearPerion;

	@GetMapping("/r/F20t3500-{m1}-{m2}-{year}")
	public  @ResponseBody Map<String, Object> readF20t3500(
			@PathVariable Integer m1
			,@PathVariable Integer m2
			,@PathVariable Integer year
			,Principal userPrincipal) {
		Map<String, Object> map = new HashMap<>();
		map.put("min_month", m1);
		map.put("max_month", m2);
		map.put("year", year);
		logger.info(" ------------------------- "
				+ "\n /r/F20t3500-"+ map +""
				+ "\n " + sqlHol1EihF20t3500All
				.replaceAll(":min_month", map.get("min_month").toString())
				.replaceAll(":max_month", map.get("max_month").toString())
				.replaceAll(":year", map.get("year").toString())
			);
		StopWatch watch = new StopWatch();
		watch.start();
		List<Map<String, Object>> list
			= hol1EihParamJdbcTemplate.queryForList(sqlHol1EihF20t3500All, map);
		map.put("list", list);
		Map<String, Object> countOperationHistory
			= hol1EihParamJdbcTemplate.queryForMap(sqlHol1EihOperationHistoryCountOfMonthYearPerion, map);
		map.put("countOperationHistory", countOperationHistory);
		watch.stop();
		map.put("duration", watch.getTotalTimeSeconds());
		System.out.println("duration = " + map.get("duration"));
		return map;
	}

	private @Value("${sql.hol1Eih.f20t3100.department}") String sqlHol1EihF20t3100Department;
	@GetMapping("/r/F20t3100-{m1}-{m2}-{year}")
	public  @ResponseBody Map<String, Object> readF20t3100(
			@PathVariable Integer m1
			,@PathVariable Integer m2
			,@PathVariable Integer year
			,Principal userPrincipal) {
		Map<String, Object> map = new HashMap<>();
		map.put("min_month", m1);
		map.put("max_month", m2);
		map.put("year", year);
		logger.info(" ------------------------- "
				+ "\n /r/F20t3100-"+ map +""
				+ "\n " + sqlHol1EihF20t3100Department
				.replaceAll(":min_month", map.get("min_month").toString())
				.replaceAll(":max_month", map.get("max_month").toString())
				.replaceAll(":year", map.get("year").toString())
				);
		StopWatch watch = new StopWatch();
		watch.start();
		
		List<Map<String, Object>> listOfDepartment
			= hol1EihParamJdbcTemplate.queryForList(sqlHol1EihF20t3100Department,map);
		map.put("listOfDepartment", listOfDepartment);
		
		watch.stop();
		map.put("duration", watch.getTotalTimeSeconds());
		System.out.println("duration = " + map.get("duration"));
		return map;
	}

	@Autowired private Environment env;

	@GetMapping("/r/history-{column}-department-{departmentId}-F20t3100-{m1}-{m2}-{year}")
	public @ResponseBody Map<String, Object> readF20t3100HistoryDepartment(
			@PathVariable Integer m1
			,@PathVariable Integer m2
			,@PathVariable Integer year
			,@PathVariable Integer departmentId
			,@PathVariable String column
			,Principal userPrincipal) {
		Map<String, Object> map = m1m2YearDepartment(m1, m2, year, departmentId);
		StopWatch watch = new StopWatch();
		watch.start();
		String sqlColumn = env.getProperty("sql.hol1Eih.f20t3100.history." + column);

		logger.info(" ------------------------- \n"
				+ "/r/history-{column}-department-{departmentId}-F20t3100-{m1}-{m2}-{year}"+ map +""
//				+ "\n " + sqlHol1EihF20t3100DepartmentId
				+ "\n " + sqlColumn
				.replaceAll(":departmentId", map.get("departmentId").toString())
				.replaceAll(":min_month", map.get("min_month").toString())
				.replaceAll(":max_month", map.get("max_month").toString())
				.replaceAll(":year", map.get("year").toString())
				);

		List<Map<String, Object>> columnHistory
			= hol1EihParamJdbcTemplate.queryForList(sqlColumn, map);

		map.put("columnHistory", columnHistory);

		watch.stop();
		map.put("duration", watch.getTotalTimeSeconds());
		System.out.println("duration = " + map.get("duration"));
		return map;
	}
	@GetMapping("/r/bedDay-{departmentId}-F20t3100-{m1}-{m2}-{year}")
	public @ResponseBody Map<String, Object> readF20t3100BedDay(
			@PathVariable Integer m1
			,@PathVariable Integer m2
			,@PathVariable Integer year
			,@PathVariable Integer departmentId
			,Principal userPrincipal) {
		Map<String, Object> map = m1m2YearDepartment(m1, m2, year, departmentId);
		logger.info(" ------------------------- \n"
				+ "/r/bedDay-{departmentId}-F20t3100-{m1}-{m2}-{year}"+ map +""
				+ "\n " + sqlHol1EihF20t3100BedDay
				.replaceAll(":departmentId", map.get("departmentId").toString())
				.replaceAll(":min_month", map.get("min_month").toString())
				.replaceAll(":max_month", map.get("max_month").toString())
				.replaceAll(":year", map.get("year").toString())
				);
		StopWatch watch = new StopWatch();
		watch.start();

		Map<String, Object> bedDay
			= hol1EihParamJdbcTemplate.queryForMap(sqlHol1EihF20t3100BedDay, map);
		map.put("bedDay", bedDay);

		watch.stop();
		map.put("duration", watch.getTotalTimeSeconds());
		System.out.println("duration = " + map.get("duration"));
		return map;
	}

	private @Value("${sql.hol1Eih.f20t3100.bedDay}") String sqlHol1EihF20t3100BedDay;
	private @Value("${sql.hol1Eih.f20t3100.departmentOut}") String sqlHol1EihF20t3100DepartmentOut;
	private @Value("${sql.hol1Eih.f20t3100.departmentIn}") String sqlHol1EihF20t3100DepartmentId;
	private @Value("${sql.hol1Eih.f20t3100.departmentIn017}") String sqlHol1EihF20t3100DepartmentId017;
	private @Value("${sql.hol1Eih.f20t3100.departmentDead}") String sqlHol1EihF20t3100DepartmentDead;
	@GetMapping("/r/department-{departmentId}-F20t3100-{m1}-{m2}-{year}")
	public @ResponseBody Map<String, Object> readF20t3100Department(
			@PathVariable Integer m1
			,@PathVariable Integer m2
			,@PathVariable Integer year
			,@PathVariable Integer departmentId
			,Principal userPrincipal) {
		Map<String, Object> map = m1m2YearDepartment(m1, m2, year, departmentId);
		logger.info(" ------------------------- "
				+ "\n /r/F20t3100-"+ map +""
//				+ "\n " + sqlHol1EihF20t3100DepartmentId
				+ "\n " + sqlHol1EihF20t3100DepartmentDead
				.replaceAll(":departmentId", map.get("departmentId").toString())
				.replaceAll(":min_month", map.get("min_month").toString())
				.replaceAll(":max_month", map.get("max_month").toString())
				.replaceAll(":year", map.get("year").toString())
				);
		StopWatch watch = new StopWatch();
		watch.start();
		
		Map<String, Object> departmentDead
		= hol1EihParamJdbcTemplate.queryForMap(sqlHol1EihF20t3100DepartmentDead, map);
		map.put("departmentDead", departmentDead);

		Map<String, Object> departmentOut
		= hol1EihParamJdbcTemplate.queryForMap(sqlHol1EihF20t3100DepartmentOut, map);
		map.put("departmentOut", departmentOut);

		Map<String, Object> departmentIn
			= hol1EihParamJdbcTemplate.queryForMap(sqlHol1EihF20t3100DepartmentId, map);
		map.put("departmentIn", departmentIn);

		Map<String, Object> departmentIn017
		= hol1EihParamJdbcTemplate.queryForMap(sqlHol1EihF20t3100DepartmentId017, map);
		map.put("departmentIn017", departmentIn017);

		watch.stop();
		map.put("duration", watch.getTotalTimeSeconds());
		System.out.println("duration = " + map.get("duration"));
		return map;
	}


	private Map<String, Object> m1m2YearDepartment(Integer m1, Integer m2, Integer year, Integer departmentId) {
		Map<String, Object> map = new HashMap<>();
		map.put("min_month", m1);
		map.put("max_month", m2);
		map.put("year", year);
		map.put("departmentId", departmentId);
		return map;
	}

	private @Value("${sql.hol1Eih.f20t3600.year_month}") String sqlHol1EihF20t3600YearMonth;
	@GetMapping("/r/F20t3600-{m1}-{m2}-{year}")
	public  @ResponseBody Map<String, Object> readF20t3600(
			@PathVariable Integer m1
			,@PathVariable Integer m2
			,@PathVariable Integer year
			,Principal userPrincipal) {
		Map<String, Object> map = new HashMap<>();
		map.put("min_month", m1);
		map.put("max_month", m2);
		map.put("year", year);
		logger.info("\n -------------------------  /r/F20t3600-"+ map);
		StopWatch watch = new StopWatch();
		watch.start();
		logger.debug("\n"+sqlHol1EihF20t3600YearMonth);
		List<Map<String, Object>> list
			= hol1EihParamJdbcTemplate.queryForList(sqlHol1EihF20t3600YearMonth,map);
		map.put("list", list);
		map.put("m1", m1);
		map.put("m2", m2);
		watch.stop();
		map.put("duration", watch.getTotalTimeSeconds());
		System.out.println("duration = " + map.get("duration"));
		return map;
	}

	private @Value("${sql.hol1Eih.readHistoryYears}") String sqlHol1EihReadHistoryYears;
	@GetMapping("/r/readHistoryYears")
	public  @ResponseBody List<Map<String, Object>> readDepartmentMotion() {
		String exampleUsePropertyHoder_pullTheProperyByNameDinamicly = propertyHolder.get("sql.hol1Eih.f20t3220.all");
		List<Map<String, Object>> historyYears
			= hol1EihJdbcTemplate.queryForList(sqlHol1EihReadHistoryYears);
		return historyYears;
	}

	
}
