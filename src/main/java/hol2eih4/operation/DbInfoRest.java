package hol2eih4.operation;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DbInfoRest {
	private static final Logger logger = LoggerFactory.getLogger(DbInfoRest.class);
	@Autowired NamedParameterJdbcTemplate hol1EihParamJdbcTemplate;
	@Autowired JdbcTemplate hol1EihJdbcTemplate;

	@Value("${sql.hol1.show-table.columns}") private String sqlHol1ShowTableColumns;
	@Value("${sql.hol1.show-table.names}") private String tableNames;
	
	@RequestMapping(value = "/v/show-tables-columns", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> ix( ) {
		logger.info("\n ------------------------- Start "+"/v/show-tables-columns");
		HashMap<String, Object> map = new HashMap<>();
		HashMap<String, Object> tables = new HashMap<>();
		map.put("tables", tables);
		String[] split = tableNames.split(", ");
		for (String tableName : split) {
			List<Map<String, Object>> tableColumns
			= hol1EihJdbcTemplate.queryForList(sqlHol1ShowTableColumns.replace(":tableName", tableName));
			tables.put(tableName, tableColumns);
		}
		return map;
	}
}
