package hol2eih4.operation;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DbInfoRest extends IxBasicRest{
	private static final Logger logger = LoggerFactory.getLogger(DbInfoRest.class);

	@Value("${sql.hol1.show-table.names}") private String tableNames;
	
	@RequestMapping(value = "/v/show-tables-columns", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> ix( ) {
		logger.info("\n ------------------------- Start "+"/v/show-tables-columns");
		HashMap<String, Object> map = new HashMap<>();
		HashMap<String, Object> tables = new HashMap<>();
		map.put("tables", tables);
		String[] split = tableNames.split(", ");
		for (String tableName : split) {
			tables.put(tableName, getTableColumns(tableName));
		}
		return map;
	}

	
}
