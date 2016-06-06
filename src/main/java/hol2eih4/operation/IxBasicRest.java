package hol2eih4.operation;

import java.security.Principal;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public class IxBasicRest {
	
	private static final Logger logger = LoggerFactory.getLogger(IxBasicRest.class);
	
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
