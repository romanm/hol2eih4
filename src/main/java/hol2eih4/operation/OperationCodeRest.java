package hol2eih4.operation;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class OperationCodeRest extends IxBasicRest{
	private static final Logger logger = LoggerFactory.getLogger(OperationCodeRest.class);
		@Autowired NamedParameterJdbcTemplate hol1EihParamJdbcTemplate;
		@Autowired JdbcTemplate hol1EihJdbcTemplate;

		@Value("${sql.hol1.department.operation}") private String sqlHol1DepartmentOperation;
		@Value("${sql.hol1.department.surgery}") private String sqlHol1DepartmentSurgery;
		@Value("${sql.hol1.department.anesthetist}") private String sqlHol1DepartmentAnesthetist;
		@Value("${sql.hol1.anesthesia}") private String sqlHol1Anestesia;
		@Value("${sql.hol1.result}") private String sqlHol1Result;
		@Value("${sql.hol1.complication}") private String sqlHol1Complication;
		@Value("${sql.hol1.complication.department}") private String sqlHol1ComplicationDepartment;
		@RequestMapping(value = "/v/operation/start-lists", method = RequestMethod.GET)
		public  @ResponseBody Map<String, Object> readOperationStarLists(Principal principal) {
			logger.info("\n ------------------------- Start /v/operation/start-lists");
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("principal", principal);
			if(principal != null){
				useAuthorityRole(principal, model);
				System.out.println(model);
				Integer departmentId = (Integer) model.get("departmentId");
				List<Map<String, Object>> departmentSurgery 
				= hol1EihParamJdbcTemplate.queryForList(sqlHol1DepartmentSurgery, 
					new MapSqlParameterSource("departmentId",  departmentId ));
				model.put("departmentSurgery", departmentSurgery);
				List<Map<String, Object>> departmentAnesthetist 
				= hol1EihParamJdbcTemplate.queryForList(sqlHol1DepartmentAnesthetist, 
						new MapSqlParameterSource("departmentId",  departmentId ));
				model.put("departmentAnesthetist", departmentAnesthetist);
				List<Map<String, Object>> anesthesia 
				= hol1EihJdbcTemplate.queryForList(sqlHol1Anestesia);
				model.put("anesthesia", anesthesia);
				
				List<Map<String, Object>> result 
				= hol1EihJdbcTemplate.queryForList(sqlHol1Result);
				model.put("result", result);
				
				List<Map<String, Object>> complication 
				= hol1EihJdbcTemplate.queryForList(sqlHol1Complication);
				model.put("complication", complication);
				
				List<Map<String, Object>> complicationDepartment 
				= hol1EihParamJdbcTemplate.queryForList(sqlHol1ComplicationDepartment, 
						new MapSqlParameterSource("departmentId",  departmentId ));
				model.put("complicationDepartment", complicationDepartment);
				
				List<Map<String, Object>> departmentOperation 
				= hol1EihJdbcTemplate.queryForList(sqlHol1DepartmentOperation);
				model.put("departmentOperation", departmentOperation);
			}
			return model;
		}

		@Value("${sql.hol1.procedure-operation.sibling}") private String sqlHol1ProcedureOperationSibling;

		@RequestMapping(value = "/v/siblingProcedureOperation/{parentId}", method = RequestMethod.GET)
		public @ResponseBody List<Map<String, Object>> getSibling(@PathVariable Integer parentId) {
			logger.info("\n ------------------------- Start /siblingProcedureOperation/"+parentId);
			System.out.println(sqlHol1ProcedureOperationSibling);
			List<Map<String, Object>> seekProcedure 
			= hol1EihParamJdbcTemplate.queryForList(sqlHol1ProcedureOperationSibling, 
					new MapSqlParameterSource("parentId",  parentId ));
			return seekProcedure;
		}
		
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
