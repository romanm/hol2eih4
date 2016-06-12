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
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class OperationCodeRest extends IxBasicRest{
	private static final Logger logger = LoggerFactory.getLogger(OperationCodeRest.class);
		@Autowired NamedParameterJdbcTemplate hol1EihParamJdbcTemplate;

		@Value("${sql.hol1Eih.update.operation_history.icd}") private String sqlHol1EihUpdateOperationHistoryIcd;
		@RequestMapping(value = "/operation-history-set-icd-{icdId}-where-{operationHistoryId}", method = RequestMethod.PUT)
		public @ResponseBody Map<String, Object> updateOperationHistoryIcd(@PathVariable Integer icdId  , @PathVariable Integer operationHistoryId) {
			logger.info("\n ------------------------- Start "
					+ "/operation-history-set-icd-"
					+ icdId
					+ "-where-"
					+ operationHistoryId
					);
			MapSqlParameterSource updateParameters = new MapSqlParameterSource();
			updateParameters.addValue("operationHistoryId", operationHistoryId);
			updateParameters.addValue("icdId", icdId);
			int update = hol1EihParamJdbcTemplate.update(sqlHol1EihUpdateOperationHistoryIcd, updateParameters);
			Map<String, Object> result = new HashMap<>();
			result.put("update", update);
			result.put("updateParameters", updateParameters.getValues());
			logger.info("\n --------"
					+ result
					);
			return result;
		}
		
		@Value("${sql.hol1Eih.update.operation_history.int.variable.value}") private String sqlHol1EihUpdateOperationHistoryIntFieldValue;
		@RequestMapping(value = "/operation-history-int-set-{fieldName}-{valueInt}-where-{operationHistoryId}", method = RequestMethod.PUT)
		public @ResponseBody Map<String, Object> updateOperationHistoryIntField(
				@PathVariable String fieldName, @PathVariable Integer valueInt , @PathVariable Integer operationHistoryId ) {
			logger.info("\n ------------------------- Start "
					+ "/operation-history-int-set-" + fieldName
					+ "-" + valueInt
					+ "-where-" + operationHistoryId
					);
			String sql = sqlHol1EihUpdateOperationHistoryIntFieldValue.replace(":fieldInt", fieldName);
			logger.info("\n --------"
					+ sql
					);
			MapSqlParameterSource updateParameters = new MapSqlParameterSource();
			updateParameters.addValue("operationHistoryId", operationHistoryId);
			updateParameters.addValue("valueInt", valueInt);
			int update = hol1EihParamJdbcTemplate.update(sql, updateParameters);
			Map<String, Object> result = new HashMap<>();
			result.put("updateParameters", updateParameters.getValues());
			result.put("update", update);
			logger.info("\n --------"
					+ result
					);
			return result;
		}
		
		@Value("${sql.hol1Eih.update.operation_history.procedure_moz}") private String sqlHol1EihUpdateOperationHistoryProcedureMoz;
		@RequestMapping(value = "/operation-history-set-procedure-{procedureMozId}-where-{operationHistoryId}", method = RequestMethod.PUT)
		public @ResponseBody Map<String, Object> updateOperationHistoryProcedureMoz(@PathVariable Integer procedureMozId , @PathVariable Integer operationHistoryId ) {
			logger.info("\n ------------------------- Start "
					+ "/operation-history-set-procedure-"
					+ procedureMozId
					+ "-where-"
					+ operationHistoryId
					);
			MapSqlParameterSource updateParameters = new MapSqlParameterSource();
			updateParameters.addValue("operationHistoryId", operationHistoryId);
			updateParameters.addValue("procedureMozId", procedureMozId);
			int update = hol1EihParamJdbcTemplate.update(sqlHol1EihUpdateOperationHistoryProcedureMoz, updateParameters);
			Map<String, Object> result = new HashMap<>();
			result.put("updateParameters", updateParameters.getValues());
			result.put("update", update);
			logger.info("\n --------"
					+ result
					);
			return result;
		}

		@RequestMapping(value = "/insertOperationHistory", method = RequestMethod.POST)
		public  @ResponseBody Map<String, Object> insertOperationHistory(@RequestBody Map<String, Object> insertOperationHistory) {
			logger.info("\n ------------------------- Start "
					+ "insertOperationHistory"
					);
			System.out.println(insertOperationHistory);
			String insertIntoTableOperationHistory = insertIntoTable(insertOperationHistory, "operation_history");
			System.out.println(insertIntoTableOperationHistory);
			hol1EihJdbcTemplate.execute(insertIntoTableOperationHistory);
			Integer historyId = Integer.parseInt((String) insertOperationHistory.get("history_id"));
			String sql = sqlHol1EihOperationHistoryHistoryId+"  ORDER BY operation_history_id DESC LIMIT 1";
			System.out.println(sql.replace(":historyId", ""+historyId));
//			Map<String, Object> historyIdOperationHistory = hol1EihJdbcTemplate.queryForMap(sql.replace(":historyId", ""+historyId));
			Map<String, Object> historyIdOperationHistory = hol1EihParamJdbcTemplate.queryForMap(sql, new MapSqlParameterSource("historyId",historyId));
			System.out.println(historyIdOperationHistory);
			return historyIdOperationHistory;
		}

		private String insertIntoTable(Map<String, Object> insertOperationHistory, String tableName) {
			List<Map<String, Object>> tableColumns = getTableColumns(tableName);
			System.out.println(tableColumns);
			String variable="", value ="";
			for (Map<String, Object> columnMap : tableColumns) {
//				System.out.println(columnMap);
				String field = (String) columnMap.get("Field");
				String type = (String) columnMap.get("Type");
				if(insertOperationHistory.containsKey(field)){
					Object fieldValue = insertOperationHistory.get(field);
					if(fieldValue == null)
						continue;
					variable += ", "+field;
					if(type.contains("datetime")){
						java.sql.Timestamp timestamp = new java.sql.Timestamp((long) fieldValue);
						value += ", '" + timestamp + "' ";
					}else
					if(type.contains("varchar"))
						value += ", '" + fieldValue + "' ";
					else
						value += ", " + fieldValue + " ";
				}
			}
			String insert = "INSERT INTO "
					+ tableName
					+ " ("
					+ variable.substring(1)
					+ ") VALUES ("
					+ value.substring(1)
					+ ")";
			return insert;
		}

		@Value("${sql.hol1.department.operation}") private String sqlHol1DepartmentOperation;
		@Value("${sql.hol1.surgery}") private String sqlHol1Surgery;
		@Value("${sql.hol1.surgery.department}") private String sqlHol1DepartmentSurgery;
		@Value("${sql.hol1.anesthetist}") private String sqlHol1Anesthetist;
		@Value("${sql.hol1.anesthetist.department}") private String sqlHol1DepartmentAnesthetist;
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
				
				List<Map<String, Object>> surgery 
				= hol1EihJdbcTemplate.queryForList(sqlHol1Surgery);
				model.put("surgery", surgery);
				
				List<Map<String, Object>> departmentAnesthetist 
				= hol1EihParamJdbcTemplate.queryForList(sqlHol1DepartmentAnesthetist, 
						new MapSqlParameterSource("departmentId",  departmentId ));
				model.put("departmentAnesthetist", departmentAnesthetist);
				
				List<Map<String, Object>> anesthetist 
				= hol1EihJdbcTemplate.queryForList(sqlHol1Anesthetist);
				model.put("anesthetist", anesthetist);
				
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
			HashMap<String, Object> result = new HashMap<>();
			HashMap<String, Integer> sqlParam = new HashMap<>();
			sqlParam.put("historyId", historyId);
			result.put("sqlParam", sqlParam);
			result.put("historyMap", hol1EihParamJdbcTemplate.queryForMap(sqlHol1EihHistoryId, sqlParam));
			logger.info("\n ------------------------- Start /ix/"
					+ historyId
					+ "\n"+sqlHol1EihOperationHistoryHistoryId);
			List<Map<String, Object>> operationHistoryList 
			= hol1EihParamJdbcTemplate.queryForList(sqlHol1EihOperationHistoryHistoryId, sqlParam);
			result.put("operationHistoryList", operationHistoryList);
			return result;
		}
		
		@Value("${sql.hol1Eih.department-patient}") private String sqlHol1EihDepartmentPatient;

		@RequestMapping(value = "/v/department-patient/{departmentId}", method = RequestMethod.GET)
		public @ResponseBody Map<String, Object> seekOperation(@PathVariable Integer departmentId) {
			logger.info("\n ------------------------- Start "
					+ "/v/department-patient/"+departmentId);
			System.out.println("---------------");
			HashMap<String, Object> map = new HashMap<>();
			HashMap<String, Integer> sqlParam = new HashMap<>();
			sqlParam.put("departmentId", departmentId);
			map.put("sqlParam", sqlParam);

			System.out.println("---------------");
			List<Map<String, Object>> departmentPatients 
				= hol1EihParamJdbcTemplate.queryForList(sqlHol1EihDepartmentPatient, sqlParam);
			System.out.println("---------------");
			System.out.println(departmentPatients);
			map.put("departmentPatients", departmentPatients);
			System.out.println("---------------");
			
			return map;
		}

}
