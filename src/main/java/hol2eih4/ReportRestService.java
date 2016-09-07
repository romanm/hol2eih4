package hol2eih4;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.StandardEnvironment;
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
	
	private @Value("${sql.hol1Eih.readHistoryYears}") String sqlHol1EihReadHistoryYears;
	@RequestMapping(value = "/r/readHistoryYears", method = RequestMethod.GET)
	public  @ResponseBody List<Map<String, Object>> readDepartmentMotion() {
		logger.info("\n ------------------------- "
				+ "/r/readHistoryYears"
				);
		String string = propertyHolder.get("sql.hol1Eih.f20t3220.all");
		System.out.println(string);
//		String sqlF20T3220Part = "2";
//		System.out.println(propertyHolder.get("sql.hol1Eih.t22."+ sqlF20T3220Part));
		List<Map<String, Object>> historyYears
			= hol1EihJdbcTemplate.queryForList(sqlHol1EihReadHistoryYears);
		return historyYears;
	}

	private void test3220(Map<String, Object> mmp) {
//		String sql = propertyHolder.get("sql.hol1Eih.history.year_month");
		String sql = propertyHolder.get("sql.hol1Eih.f20t3220.year_month");
		System.out.println(sql);
		System.out.println(sql.length());
		List<Map<String, Object>> queryForList = hol1EihParamJdbcTemplate.queryForList(sql,mmp);
		mmp.put("list", queryForList);
	}

	@RequestMapping(value = "/r/F20t3220-{m1}-{m2}-{year}", method = RequestMethod.GET)
	public  @ResponseBody Map<String, Object> readF20t3220(
			@PathVariable Integer m1
			,@PathVariable Integer m2
			,@PathVariable Integer year
			,Principal userPrincipal) {
		StopWatch watch = new StopWatch();
		watch.start();
		Map<String, Object> map = new HashMap<>();
		map.put("min_month", m1);
		map.put("max_month", m2);
		map.put("year", year);
		logger.info("\n -------------------------  /r/F20t3220- " + map);
		test3220(map);
		map.put("m1", m1);
		map.put("m2", m2);
		
//		List<Map<String, Object>> bedDayOfMonthMySql = appService.readBedDayOfMonthMySql(m1,m2);
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
//		List<Map<String, Object>> bedDayOfMonthMySql = appService.readBedDayOfMonthMySql(m1,m2);
		List<Map<String, Object>> historyYears
		= hol1EihJdbcTemplate.queryForList(sqlHol1EihReadHistoryYears);
		watch.stop();
		map.put("duration", watch.getTotalTimeSeconds());
		System.out.println("duration = " + map.get("duration"));
		map.put("list", historyYears);
		return map;
	}
	
}
