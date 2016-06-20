package hol2eih4.operation;

import java.security.Principal;
import java.sql.Timestamp;
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

		@Value("${sql.hol1Eih.update.icd.icd10uatree_with_parent_name}") private String sqlHol1EihUpdateIcd10uatreeWithParentName;
		@RequestMapping(value = "/icd10uatree_with_parent_name/{icdId}", method = RequestMethod.PUT)
		public @ResponseBody Map<String, Object> addGroupName(@PathVariable Integer icdId ) {
			logger.info("\n ------------------------- Start "
					+ "/icd10uatree_with_parent_name/"
					+ icdId
					);
			MapSqlParameterSource updateParameters = new MapSqlParameterSource();
			updateParameters.addValue("icdId", icdId);
			int update = hol1EihParamJdbcTemplate.update(sqlHol1EihUpdateIcd10uatreeWithParentName, updateParameters);
			Map<String, Object> result = new HashMap<>();
			result.put("update", update);
			result.put("updateParameters", updateParameters.getValues());
			logger.info("\n --------"
					+ result
					);
			return result;
		}
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
		
		@Value("${sql.hol1Eih.update.operation_history.type.variable.value}") private String sqlHol1EihUpdateOperationHistoryTypeFieldValue;
		@RequestMapping(value = "/operation-history-strfield-where-{operationHistoryId}", method = RequestMethod.PUT)
		public @ResponseBody Map<String, Object> updateOperationHistoryStringField( @PathVariable Integer operationHistoryId , @RequestBody Map<String, Object> insertOperationHistory) {
			logger.info("\n ------------------------- Start "
					+ "/operation-history-strfield-where-" + operationHistoryId
					+"\n --------"
					+ insertOperationHistory
					);
			String fieldStr = (String) insertOperationHistory.get("field");
			String sql = sqlHol1EihUpdateOperationHistoryTypeFieldValue.replace(":field", fieldStr);
			logger.info("\n --------"
					+ sql
					);
			MapSqlParameterSource updateParameters = new MapSqlParameterSource();
			updateParameters.addValue("operationHistoryId", operationHistoryId);
			updateParameters.addValue("value", (String) insertOperationHistory.get("value"));
			int update = hol1EihParamJdbcTemplate.update(sql, updateParameters);
			Map<String, Object> result = new HashMap<>();
			result.put("updateParameters", updateParameters.getValues());
			result.put("update", update);
			logger.info("\n --------"
					+ result
					);
			return result;
		}
		@RequestMapping(value = "/operation-history-int-set-{fieldInt}-{valueInt}-where-{operationHistoryId}", method = RequestMethod.PUT)
		public @ResponseBody Map<String, Object> updateOperationHistoryIntField(
				@PathVariable String fieldInt, @PathVariable Integer valueInt , @PathVariable Integer operationHistoryId ) {
			logger.info("\n ------------------------- Start "
					+ "/operation-history-int-set-" + fieldInt
					+ "-" + valueInt
					+ "-where-" + operationHistoryId
					);
			String sql = sqlHol1EihUpdateOperationHistoryTypeFieldValue.replace(":field", fieldInt);
			logger.info("\n --------"
					+ sql
					);
			MapSqlParameterSource updateParameters = new MapSqlParameterSource();
			updateParameters.addValue("operationHistoryId", operationHistoryId);
			updateParameters.addValue("value", valueInt);
			int update = hol1EihParamJdbcTemplate.update(sql, updateParameters);
			Map<String, Object> result = new HashMap<>();
			result.put("updateParameters", updateParameters.getValues());
			result.put("update", update);
			logger.info("\n --------"
					+ result
					);
			return result;
		}
		
		@Value("${sql.hol1Eih.update.operation_history.operation_history_start}") private String sqlHol1EihUpdateOperationHistoryStart;
		@Value("${sql.hol1Eih.update.operation_history.operation_history_end}") private String sqlHol1EihUpdateOperationHistoryEnd;
		@RequestMapping(value = "/operation-history-set-start-{operationHistoryStart}-where-{operationHistoryId}", method = RequestMethod.PUT)
		public @ResponseBody Map<String, Object> updateOperationHistoryStart(@PathVariable Long operationHistoryStart , @PathVariable Integer operationHistoryId ) {
			logger.info("\n ------------------------- Start "
					+ "/operation-history-set-start-"
					+ operationHistoryStart
					+ "-where-"
					+ operationHistoryId
					);
			MapSqlParameterSource updateParameters = new MapSqlParameterSource();
			Timestamp operationHistoryStartTS = new Timestamp(operationHistoryStart);
			updateParameters.addValue("operationHistoryStartTS", operationHistoryStartTS);
			updateParameters.addValue("operationHistoryId", operationHistoryId);
			int update = hol1EihParamJdbcTemplate.update(sqlHol1EihUpdateOperationHistoryStart, updateParameters);
			update += hol1EihParamJdbcTemplate.update(sqlHol1EihUpdateOperationHistoryEnd, new MapSqlParameterSource("operationHistoryId", operationHistoryId));
			Map<String, Object> result = new HashMap<>();
			result.put("updateParameters", updateParameters.getValues());
			result.put("update", update);
			logger.info("\n --------"
					+ result
					);
			return result;
		}
		@Value("${sql.hol1Eih.update.operation_history.operation_history_duration}") private String sqlHol1EihUpdateOperationHistoryDuration;
		@RequestMapping(value = "/operation-history-set-duration-{operationHistoryDuration}-where-{operationHistoryId}", method = RequestMethod.PUT)
		public @ResponseBody Map<String, Object> updateOperationHistoryDuration(@PathVariable Integer operationHistoryDuration , @PathVariable Integer operationHistoryId ) {
			logger.info("\n ------------------------- Start "
					+ "/operation-history-set-duration-"
					+ operationHistoryDuration
					+ "-where-"
					+ operationHistoryId
					);
			MapSqlParameterSource updateParameters = new MapSqlParameterSource();
			updateParameters.addValue("operationHistoryId", operationHistoryId);
			updateParameters.addValue("operationHistoryDuration", operationHistoryDuration);
			int update = hol1EihParamJdbcTemplate.update(sqlHol1EihUpdateOperationHistoryDuration, updateParameters);
			Map<String, Object> result = new HashMap<>();
			result.put("updateParameters", updateParameters.getValues());
			result.put("update", update);
			logger.info("\n --------"
					+ result
					);
			return result;
		}
		@Value("${sql.hol1Eih.insert.operation_history.procedure_moz}") private String sqlHol1EihInsertOperationHistoryProcedureMoz;
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
			int updateInsert = hol1EihParamJdbcTemplate.update(sqlHol1EihUpdateOperationHistoryProcedureMoz, updateParameters);
			if(updateInsert == 0)
			{
				updateInsert = hol1EihParamJdbcTemplate.update(sqlHol1EihInsertOperationHistoryProcedureMoz, updateParameters);
			}
			Map<String, Object> result = new HashMap<>();
			result.put("updateParameters", updateParameters.getValues());
			result.put("update", updateInsert);
			logger.info("\n --------"
					+ result
					);
			return result;
		}

		@Value("${sql.hol1Eih.operation_history.delete}") private String sqlHol1EihOperationHistoryDelete;
		@RequestMapping(value = "/deleteOperationHistory-{operationHistoryId}", method = RequestMethod.POST)
		public  @ResponseBody Map<String, Object> deleteOperationHistory(@PathVariable Integer operationHistoryId) {
			logger.info("\n ------------------------- Start "
					+ "/deleteOperationHistory-"+operationHistoryId
					);
			int update = hol1EihParamJdbcTemplate.update(sqlHol1EihOperationHistoryDelete, new MapSqlParameterSource("operationHistoryId",operationHistoryId));
			Map<String, Object> result = new HashMap<>();
			result.put("update", update);
			return result;
		}
		@Value("${sql.hol1Eih.select.icd.start-end}") private String sqlHol1EihSelectIcdStartEnd;
		@RequestMapping(value = "/insertOperationHistory", method = RequestMethod.POST)
		public  @ResponseBody Map<String, Object> insertOperationHistory(@RequestBody Map<String, Object> insertOperationHistory) {
			logger.info("\n ------------------------- Start "
					+ "insertOperationHistory"
					);
			System.out.println("insertOperationHistory");
			System.out.println(insertOperationHistory);
			Map<String, Object> icdStartEnd 
			= hol1EihParamJdbcTemplate.queryForMap(sqlHol1EihSelectIcdStartEnd, new MapSqlParameterSource("icdId",insertOperationHistory.get("icd_id")));
			insertOperationHistory.putAll(icdStartEnd);
			System.out.println("icdStartEnd");
			System.out.println(icdStartEnd);
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
		@Value("${sql.hol1.operation_result}") private String sqlHol1OperationResult;
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
				= hol1EihJdbcTemplate.queryForList(sqlHol1OperationResult);
				model.put("operation_result", result);
				
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
			List<Map<String, Object>> operationHistoryList = getOperation(historyId);
			result.put("operationHistoryList", operationHistoryList);
			Map<String, Object> map = operationHistoryList.get(0);
			System.out.println(map.get("icd_name"));
			System.out.println(map.get("icd_code"));
			System.out.println(map.get("icd10uatree_id"));
			
			return result;
		}
		@RequestMapping(value = "/v/ix-operation/{historyId}", method = RequestMethod.GET)
		public @ResponseBody List<Map<String, Object>> getOperation(@PathVariable Integer historyId) {
			logger.info("\n ------348------------sql--"
					+ historyId
					+ "\n"+sqlHol1EihOperationHistoryHistoryId.replace(":historyId", ""+historyId));
			List<Map<String, Object>> operationHistoryList 
			= hol1EihParamJdbcTemplate.queryForList(sqlHol1EihOperationHistoryHistoryId, new MapSqlParameterSource("historyId", historyId));
			logger.info(""+operationHistoryList);
			return operationHistoryList;
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
