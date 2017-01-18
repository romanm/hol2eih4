package hol2eih4;

import java.security.Principal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MaternityRest {
	private static final Logger logger = LoggerFactory.getLogger(MaternityRest.class);

	@Autowired private JdbcTemplate pgDbMaternityHolJdbcTemplate;
	@Autowired private NamedParameterJdbcTemplate pgDbMaternityHolParamJdbcTemplate;
	private @Value("${sql.pgDbMaternityHol.icdMonth}") String pgDbMaternityHolIcdMonth;

	@GetMapping(value = "/r/doSomething")
	public  @ResponseBody Map<String, Object> doSomething() {
		Map<String, Object> map = new HashMap<>();
		map.put("i", 1);
		x1(map);
		return map;
	}

	private @Value("${sql.pgDbMaternityHol.insertRuhTimestamp}") String pgDbMaternityHolInsertRuhTimestamp;
	private void x1(Map<String, Object> map) {
		DateTime dateTimeFrom = new DateTime(2016,1,1,9,0);
		DateTime dateTimeTo = dateTimeFrom.plusYears(20);
		map.put("dateTimeFrom", dateTimeFrom);
		map.put("dateTimeTo", dateTimeTo);
		logger.info("-----------------\n"
				+ "/r/doSomething "
				+ dateTimeFrom + "\n"
				+ dateTimeTo);
		while (dateTimeFrom.isBefore(dateTimeTo)) {
			map.put("daybegin", new Timestamp(dateTimeFrom.getMillis()) );
			System.out.println(map.get("daybegin"));
			pgDbMaternityHolParamJdbcTemplate.update(pgDbMaternityHolInsertRuhTimestamp, map);
			/*
			String sqlInsertRuhTimestamp = "insert into ruhtimestamp values("
					+ i
					+ ","
					+ "'"
					+ dateTimeFrom
					+ "'"
					+ ")";
			System.out.println(sqlInsertRuhTimestamp);
			System.out.println(i+"/"+dateTimeFrom);
			 * */
			dateTimeFrom = dateTimeFrom.plusDays(1);
		}
	}

	@RequestMapping(value = "/r/readIcd10K2-{m1}-{m2}", method = RequestMethod.GET)
	public  @ResponseBody Map<String, Object> readIcd10K2(
			@PathVariable Integer m1
			,@PathVariable Integer m2
			,Principal userPrincipal) {
		Map<String, Object> map = new HashMap<>();
		map.put("min_month", m1);
		map.put("max_month", m2);
		logger.debug("\n" + "/r/readIcd10K2-{m1}-{m2}" + map);
//		List<Map<String, Object>> icd10K1 = appService.readIcd10K1(m1,m2);
		List<Map<String, Object>> icd10K1
			= pgDbMaternityHolParamJdbcTemplate.queryForList(pgDbMaternityHolIcdMonth,map);
		map.put("icd10K1", icd10K1);
		if(m1 < m2) {
			List<Object> someMonth = new ArrayList<>();
			for (int i = m1; i <= m2; i++) {
				Map<String, Integer> mmp = new HashMap<>();
				mmp.put("min_month", i);
				mmp.put("max_month", i);
				List<Map<String, Object>> icd10K1Month1 
					= pgDbMaternityHolParamJdbcTemplate.queryForList(pgDbMaternityHolIcdMonth,mmp);
				someMonth.add(icd10K1Month1);
			}
			map.put("someMonth", someMonth);
		}
		map.put("m1", m1);
		map.put("m2", m2);
		return map;
	}

}

