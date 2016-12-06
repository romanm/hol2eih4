package hol2eih4.operation;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.ui.Model;

public class IxBasicRest {
	
	private static final Logger logger = LoggerFactory.getLogger(IxBasicRest.class);
	@Autowired JdbcTemplate hol1EihJdbcTemplate;
	@Value("${sql.hol1.show-table.columns}") private String sqlHol1ShowTableColumns;

	protected List<Map<String, Object>> getTableColumns(String tableName) {
		List<Map<String, Object>> tableColumns
		= hol1EihJdbcTemplate.queryForList(sqlHol1ShowTableColumns.replace(":tableName", tableName));
		return tableColumns;
	}

	private @Value("${config.staticUrlPrefix}") String staticUrlPrefix;

	protected void initModel(Model model) {
		model.addAttribute("staticUrlPrefix", staticUrlPrefix);
//		model.addAttribute("staticUrlPrefix", AppConfig.staticUrlPrefix);
	}

	void useAuthorityRole(Principal principal, Map<String, Object> model) {
		for (GrantedAuthority grantedAuthority : ((Authentication) principal).getAuthorities()) {
			System.out.println(grantedAuthority);
			for (String string : grantedAuthority.getAuthority().split("_"))
				if(string.contains("dep-")){
					int departmentId = Integer.parseInt(string.replace("dep-", ""));
					logger.info("\n ------------------------- departmentId = "+departmentId);
					model.put("departmentId", departmentId);
				}else if(string.contains("per-")){
					int personalId = Integer.parseInt(string.replace("per-", ""));
					model.put("personalId", personalId);
					logger.info("\n ------------------------- personInDepartmentId = "+personalId);
				}
		}
	}
}
