package hol2eih4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
	private static final Logger logger = LoggerFactory.getLogger(Application.class);
	public static void main(String[] args) throws Throwable {
		logger.debug("------main--3.7.3-------");
		SpringApplication.run(Application.class, args);
//		logger.debug("------main---------"+ initComponent.getInit());
	}
	@Autowired private static InitComponent initComponent;

	/*
 
	@Bean
	public TomcatEmbeddedServletContainerFactory tomcatFactory() {
		return new TomcatEmbeddedServletContainerFactory() {

			@Override
			protected TomcatEmbeddedServletContainer getTomcatEmbeddedServletContainer(
					Tomcat tomcat) {
				logger.debug("------getTomcatEmbeddedServletContainer---------");
				tomcat.enableNaming();
				return super.getTomcatEmbeddedServletContainer(tomcat);
			}

			@Override
			protected void postProcessContext(Context context) {
				logger.debug("------postProcessContext---------"+context);
				ContextResource resource = new ContextResource();
				resource.setName("jdbc/hol1dsMySql");
				resource.setType(DataSource.class.getName());
				resource.setProperty("driverClassName", "com.mysql.jdbc.Driver");
				resource.setProperty("url", "jdbc:mysql://localhost/hol?useUnicode=true&characterEncoding=utf-8");
				resource.setProperty("user", "hol");
				resource.setProperty("username", "hol");
				resource.setProperty("password", "hol");
				logger.debug("------postProcessContext---------"+resource);
				context.getNamingResources().addResource(resource);
				logger.debug("------postProcessContext---------"+context);
			}
		};
	}
	* */

	/*
	@Bean
	public DataSource jndiDataSource() throws IllegalArgumentException, NamingException {
		logger.debug("------jndiDataSource---------");
		JndiObjectFactoryBean bean = new JndiObjectFactoryBean();
		logger.debug("------jndiDataSource---------"+bean);
//		bean.setJndiName("java:jdbc/hol1dsMySql");
		bean.setJndiName("java:comp/env/jdbc/hol1dsMySql");
		bean.setProxyInterface(DataSource.class);
		bean.setLookupOnStartup(false);
		bean.afterPropertiesSet();
		logger.debug("------jndiDataSource---------"+bean);
		final Object object = bean.getObject();
		logger.debug("------jndiDataSource---------"+object);
		return (DataSource)object;
	}
	 * */
	
}
