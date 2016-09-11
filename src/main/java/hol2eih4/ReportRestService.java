package hol2eih4;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StopWatch;
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
	
	private @Value("${sql.hol1Eih.f20t3220.nrr_patients.year_month}") String sqlHol1EihF20t3220NrrPatientsYearMonth;
	@RequestMapping(value = "/r/F20t3220NrrPatienten-{m1}-{m2}-{year}-{nrr}", method = RequestMethod.GET)
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
		logger.info("\n "+sqlHol1EihF20t3220NrrPatientsYearMonth);
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
	@RequestMapping(value = "/r/F20t3220-{m1}-{m2}-{year}", method = RequestMethod.GET)
	public  @ResponseBody Map<String, Object> readF20t3220(
			@PathVariable Integer m1
			,@PathVariable Integer m2
			,@PathVariable Integer year
			,Principal userPrincipal) {
		logger.info("\n -------------------------  /r/F20t3220");
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
		logger.info("\n -------------------------  /r/F20t3220- " + map);
		String property = "sql.hol1Eih.f20t3220.history_diagnos-icd.11_3";
		logger.info("\n property: "+ property+ "\n " + propertyHolder.get(property)+ "\n " );
		System.out.println(sqlHol1Eih_TO_F20t3220YearMonth);
		System.out.println(sqlHol1Eih_TO_F20t3220YearMonth.length());
		List<Map<String, Object>> queryForList = hol1EihParamJdbcTemplate.queryForList(sqlHol1Eih_TO_F20t3220YearMonth,map);
		map.put("list", queryForList);
		map.put("m1", m1);
		map.put("m2", m2);
		watch.stop();
		map.put("duration", watch.getTotalTimeSeconds());
		System.out.println("duration = " + map.get("duration"));
		return map;
	}

	@RequestMapping(value = "/r/F20t3600-{m1}-{m2}-{year}", method = RequestMethod.GET)
	public  @ResponseBody Map<String, Object> readF20t3600(
			@PathVariable Integer m1
			,@PathVariable Integer m2
			,@PathVariable Integer year
			,Principal userPrincipal) {
		Map<String, Object> map = new HashMap<>();
		map.put("m1", m1);
		map.put("m2", m2);
		map.put("year", year);
		logger.info("\n -------------------------  /r/F20t3600-"+ map);
		StopWatch watch = new StopWatch();
		watch.start();
		List<Map<String, Object>> historyYears
		= hol1EihJdbcTemplate.queryForList(sqlHol1EihReadHistoryYears);
		watch.stop();
		map.put("duration", watch.getTotalTimeSeconds());
		System.out.println("duration = " + map.get("duration"));
		map.put("list", historyYears);
		return map;
	}

	private @Value("${sql.hol1Eih.readHistoryYears}") String sqlHol1EihReadHistoryYears;
	@RequestMapping(value = "/r/readHistoryYears", method = RequestMethod.GET)
	public  @ResponseBody List<Map<String, Object>> readDepartmentMotion() {
		String string = propertyHolder.get("sql.hol1Eih.f20t3220.all");
		List<Map<String, Object>> historyYears
			= hol1EihJdbcTemplate.queryForList(sqlHol1EihReadHistoryYears);
		return historyYears;
	}

	
}
