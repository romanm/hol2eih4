package hol2eih4.eix;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static java.lang.Math.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class EixRest {
	private static final Logger logger = LoggerFactory.getLogger(EixRest.class);
	@Autowired NamedParameterJdbcTemplate hol1EihParamJdbcTemplate;
	@Autowired JdbcTemplate hol1EihJdbcTemplate;

	@Value("${sql.hol1.ruh.f007allDepartment}") private String sqlHol1RuhF007allDepartment;
	@GetMapping(value = "/v/departmentPatientsMove-{yyyy}-{mm}-{dd}")
	public  @ResponseBody Map<String, Object> departmentPatientsMove_yyyymmdd(
			@PathVariable Integer yyyy , @PathVariable Integer mm, @PathVariable Integer dd
			,Principal principal) {
		Map<String, Object> map =  new HashMap<>();
		map.put("yyyy", yyyy);
		map.put("mm", mm);
		map.put("dd", dd);
		logger.info("\n ------ " + "/v/departmentPatientsMove-{yyyy}-{mm}-{dd}\n" + map + "\n" 
		+ sqlHol1RuhF007allDepartment.replace(":yyyy", ""+yyyy).replace(":mm", ""+mm).replace(":dd", ""+dd));
		StopWatch watch = new StopWatch();
		watch.start();
		List<Map<String, Object>> listDayRuhInSpital 
			= hol1EihParamJdbcTemplate.queryForList(sqlHol1RuhF007allDepartment, map);
		map.put("listDayRuhInSpital", listDayRuhInSpital);
		watch.stop();
		map.put("duration", watch.getTotalTimeSeconds());
		System.out.println("duration = " + map.get("duration"));
		return map;
	}

	@Value("${sql.hol1.bed_day_all_department}") private String sqlHol1BedDayAllDepartment;
	@GetMapping("/r/readBedDayAllDepartment-{m1}-{m2}-{year}")
	public  @ResponseBody Map<String, Object> readBedDayAllDepartment(
			 @PathVariable Integer m1
			 ,@PathVariable Integer m2
			 ,@PathVariable Integer year
			,Principal userPrincipal) {
		Map<String, Object> map = new HashMap<>();
		map.put("min_month", min(m1, m2));
		map.put("max_month", max(m1, m2));
		map.put("year", year);
		StopWatch watch = new StopWatch();
		watch.start();
		List<Map<String, Object>> bedDayAllDepartment
			= hol1EihParamJdbcTemplate.queryForList(sqlHol1BedDayAllDepartment, map);
		watch.stop();
		map.put("duration", watch.getTotalTimeSeconds());
		System.out.println("duration = " + map.get("duration"));
		map.put("bedDayAllDepartment", bedDayAllDepartment);
		return map;
	}

	@Value("${sql.hol1.countries}") private String sqlHol1EihCountries;
	@GetMapping("/v/eix/test")
	public @ResponseBody Map<String, Object> ixWithId() {
		logger.info("\n ------ /v/eix/test");
		Map<String, Object> map =  new HashMap<>();
		map.put("test", "eix");
		List<Map<String, Object>> listCountries = hol1EihJdbcTemplate.queryForList(sqlHol1EihCountries);
		map.put("listCountries", listCountries);
		List<Map<String, Object>> listBedDay = hol1EihJdbcTemplate.queryForList(sqlHol1BedDayAllDepartment);
		map.put("listBedDay", listBedDay);
		return map;
	}

}
