package hol2eih4;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class PologoveRest {
	private static final Logger logger = LoggerFactory.getLogger(PologoveRest.class);
	@Autowired private NamedParameterJdbcTemplate pgDbMaternityHolParamJdbcTemplate;

	private @Value("${sql.pgDbMaternityHol.operation.groupCodenumber}") 
		String pgDbMaternityHolOperationGroupCodenumber;
	private @Value("${sql.pgDbMaternityHol.monthYear.operation}") String pgDbMaternityHolMonthYearOperation;
	@GetMapping("/r/pologove/operation-{m1}-{m2}-{year}")
	public  @ResponseBody Map<String, Object> readPologoveOperation(
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
		logger.info(" ------------ \n"
				+ "/r/pologove/operation-{m1}-{m2}-{year}" + map + ""
//						+ "\n " + pgDbMaternityHolOperationGroupCodenumber
						+ "\n " + pgDbMaternityHolMonthYearOperation
						.replaceAll(":min_month", map.get("min_month").toString())
						.replaceAll(":max_month", map.get("max_month").toString())
						.replaceAll(":year", map.get("year").toString())
						);
		List<Map<String, Object>> maternityHolMonthYearOperation
//		= pgDbMaternityHolParamJdbcTemplate.queryForList(pgDbMaternityHolOperationGroupCodenumber, map);
		= pgDbMaternityHolParamJdbcTemplate.queryForList(pgDbMaternityHolMonthYearOperation,map);
		map.put("maternityHolMonthYearOperation", maternityHolMonthYearOperation);
		watch.stop();
		map.put("duration", watch.getTotalTimeSeconds());
		System.out.println("duration = " + map.get("duration"));
		return map;
	}

	private @Value("${sql.pgDbMaternityHol.monthYear.diagnose}") String pgDbMaternityHolMonthYearDiagnose;
	@GetMapping("/r/pologove/icd10-{m1}-{m2}-{year}")
	public  @ResponseBody Map<String, Object> readPologoveIcd10(
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
		logger.info(" ------------------------- \n"
				+ "/r/pologove/icd10-{m1}-{m2}-{year}" + map);
		List<Map<String, Object>> maternityHolIcdMonth
			= pgDbMaternityHolParamJdbcTemplate.queryForList(pgDbMaternityHolMonthYearDiagnose,map);
		map.put("maternityHolIcdMonth", maternityHolIcdMonth);
		watch.stop();
		map.put("duration", watch.getTotalTimeSeconds());
		System.out.println("duration = " + map.get("duration"));
		return map;
	}

}
