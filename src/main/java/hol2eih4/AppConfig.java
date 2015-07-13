package hol2eih4;

import java.text.SimpleDateFormat;

public class AppConfig {
	public final static SimpleDateFormat yyyyMMddDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	public final static SimpleDateFormat ddMMyyyDateFormat = new SimpleDateFormat("dd-MM-yyyy");
	final static String jsonDbFiles	= "src/main/webapp/db/";
	//product
//	final static String applicationFolderPfad	= "/home/hol2/server4/hol2eih4/";
//	final static String urlDb = "jdbc:h2:tcp://localhost/db-hol2-eih/db-hol2-eih";
	//development
	final static String urlDb = "jdbc:h2:tcp://localhost/db-hol2-eih/db-hol2-eih";
	final static String applicationFolderPfad	= "/home/roman/algoritmed.com/development/hol2eih4/";

	//all
	final static String applicationResourcesFolderPfad	= applicationFolderPfad+"src/main/resources/";
}
