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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ReportGeneralRest {
	private static final Logger logger = LoggerFactory.getLogger(ReportGeneralRest.class);
	@Autowired private JdbcTemplate hol1EihJdbcTemplate;
	@Autowired private NamedParameterJdbcTemplate hol1EihParamJdbcTemplate;

	@Value("${sql.report.general}") public String sqlReportGeneral;
	@RequestMapping(value = "/r/report-general-{m1}-{m2}", method = RequestMethod.GET)
	public  @ResponseBody Map<String, Object> reportGeneral(
			@PathVariable Integer m1
			,@PathVariable Integer m2
			,Principal userPrincipal) {
		Map<String, Integer> mmp = new HashMap<>();
		mmp.put("maxMonth", Math.max(m1, m2));
		mmp.put("minMonth", Math.min(m1, m2));
		logger.debug(sqlReportGeneral);
		logger.debug(sqlReportGeneral.replace(":maxMonth", ""+Math.max(m1, m2)).replace(":minMonth",""+Math.min(m1, m2)));
		logger.debug(""+mmp);
		Map map = new HashMap<String, Object>();
		List<Map<String, Object>> reportGeneral 
		= hol1EihParamJdbcTemplate.queryForList(sqlReportGeneral, mmp);
		map.put("reportGeneral", reportGeneral);
		return map;
	}

}
