package hol2eih4;

import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component("hol1DbService")
public class Hol1DbService {
	private static final Logger logger = LoggerFactory.getLogger(Hol1DbService.class);
	private JdbcTemplate jdbcTemplate;
	public Hol1DbService() throws NamingException{
		initMySql();
	}
	private void initMySql() throws NamingException {
		/*
		final DataSource dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/hol1dsMySql");
		logger.debug("\n------------initMySql-------------\n"
			+ "dataSource="+dataSource+
			"\n------------initMySql-------------");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		 * */
	}
}
