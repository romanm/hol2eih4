package hol2eih4.operation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		@Autowired NamedParameterJdbcTemplate hol1EihParamJdbcTemplate;
		
		@Value("${sql.hol1Eih.history.id}") private String sqlHol1EihHistoryId;
		
		@RequestMapping(value = "/v/ix/{historyId}", method = RequestMethod.GET)
		public @ResponseBody Map<String, Object> ix(@PathVariable Integer historyId) {
			HashMap<String, Object> map = new HashMap<>();
			HashMap<String, Integer> sqlParam = new HashMap<>();
			sqlParam.put("historyId", historyId);
			map.put("sqlParam", sqlParam);
			Map<String, Object> historyMap = hol1EihParamJdbcTemplate.queryForMap(sqlHol1EihHistoryId, sqlParam);
			map.put("historyMap", historyMap);
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
