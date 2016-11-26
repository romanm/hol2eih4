package hol2eih4;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MaternityRest {
	private static final Logger logger = LoggerFactory.getLogger(MaternityRest.class);

	@Autowired private NamedParameterJdbcTemplate pgDbMaternityHolParamJdbcTemplate;
	private @Value("${sql.pgDbMaternityHol.icdMonth}") String pgDbMaternityHolIcdMonth;

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

