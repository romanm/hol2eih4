package hol2eih4;

import java.text.SimpleDateFormat;

public class AppConfig {

	public final static SimpleDateFormat yyyyMMddDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	public final static SimpleDateFormat ddMMyyyDateFormat = new SimpleDateFormat("dd-MM-yyyy");
	public final static SimpleDateFormat yyyyMMddHHmmssDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	public final static String hol2webHost = "http://hol.in.ua:8084";
//	public final static String hol2webHost = "http://localhost:8084";
	final static String jsonDbFiles	= "src/main/webapp/db/";
	//product
//	final static String applicationFolderPfad	= "/home/hol2/server4/hol2eih4/";
//	final static String urlDb = "jdbc:h2:tcp://localhost/db-hol2-eih/db-hol2-eih";
//	final static String applicationExcelFolderPfad	= "/home/hol2/db-h2/excel/";
	//development
	final static String urlDb = "jdbc:h2:tcp://localhost/db-hol2-eih/db-hol2-eih";
	final static String applicationFolderPfad = "/home/roman/algoritmed.com/development/hol2eih4/";
	final static String applicationExcelFolderPfad = "/home/roman/algoritmed.com/h2-server/db-hol2-eih/";

	//all
	final static String excelFileName = "pyx-2015-v.2.xls";
	final static String applicationResourcesFolderPfad	= applicationFolderPfad+"src/main/resources/";
	final static String innerExcelFolderPfad	= applicationFolderPfad + "src/main/webapp/excel/";

}
