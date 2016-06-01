package hol2eih4.operation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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
public class OperationCodeRest {
	private static final Logger logger = LoggerFactory.getLogger(OperationCodeRest.class);
		@Autowired NamedParameterJdbcTemplate hol1EihParamJdbcTemplate;
		
		@Value("${sql.hol1Eih.history.id}") private String sqlHol1EihHistoryId;
		@Value("${sql.hol1Eih.operation_history.history_id}") private String sqlHol1EihOperationHistoryHistoryId;
		
		@RequestMapping(value = "/v/ix/{historyId}", method = RequestMethod.GET)
		public @ResponseBody Map<String, Object> ix(@PathVariable Integer historyId, HttpServletRequest request) {
			logger.info("\n ------------------------- Start /ix/"+historyId);
			System.out.println(request.getQueryString());
			HashMap<String, Object> map = new HashMap<>();
			HashMap<String, Integer> sqlParam = new HashMap<>();
			sqlParam.put("historyId", historyId);
			map.put("sqlParam", sqlParam);
			map.put("historyMap", hol1EihParamJdbcTemplate.queryForMap(sqlHol1EihHistoryId, sqlParam));
			List<Map<String, Object>> operationHistoryList 
			= hol1EihParamJdbcTemplate.queryForList(sqlHol1EihOperationHistoryHistoryId, sqlParam);
			System.out.println(operationHistoryList);
			map.put("operationHistoryList", operationHistoryList);
			return map;
		}
		
		@Value("${sql.hol1Eih.department-patient}") private String sqlHol1EihDepartmentPatient;

		@RequestMapping(value = "/v/department-patient/{departmentId}", method = RequestMethod.GET)
		public @ResponseBody Map<String, Object> seekOperation(@PathVariable Integer departmentId) {
			HashMap<String, Object> map = new HashMap<>();
			HashMap<String, Integer> sqlParam = new HashMap<>();
			sqlParam.put("departmentId", departmentId);
			map.put("sqlParam", sqlParam);
			
			List<Map<String, Object>> departmentPatients 
				= hol1EihParamJdbcTemplate.queryForList(sqlHol1EihDepartmentPatient, sqlParam);
			map.put("departmentPatients", departmentPatients);
			
			return map;
		}
		

}
