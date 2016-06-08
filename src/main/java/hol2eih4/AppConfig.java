package hol2eih4;

import java.text.SimpleDateFormat;
import org.joda.time.DateTime;

public class AppConfig {
	//product server /home/hol2/db-h2
//	final static String applicationFolderPfad	= "/home/hol2/server4/hol2eih4/";
//	final static String applicationExcelFolderPfad	= "/home/hol2/db-h2/excel/";
//	public final static String  staticUrlPrefix = "http://192.168.4.10";
	//development server /home/roman/algoritmed.com/h2-server
	final static String applicationFolderPfad = "/home/roman/dev-20160518/research_2/hol2eih4/";
	final static String applicationExcelFolderPfad = "/home/roman/db-java/h2-server/db-hol2-eih-excel/";
	public final static String staticUrlPrefix = "http://127.0.0.1";
	
	
//	public final static String  staticUrlPrefix = "http://localhost";
	public final static SimpleDateFormat yyyyMMddDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	public final static SimpleDateFormat ddMMyyyDateFormat = new SimpleDateFormat("dd-MM-yyyy");
	public final static SimpleDateFormat yyyyMMddHHmmssDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	public final static String hol2webHost = "http://hol.in.ua:8084";
//	public final static String hol2webHost = "http://localhost:8084";
	//-----------forAll--------------------
	final static String jsonDbFiles	= "src/main/webapp/db/";
	final static String urlH2Db = "jdbc:h2:tcp://localhost/db-hol2-eih/db-hol2-eih";
	final static String urlK1Db = "jdbc:hsqldb:hsql://localhost/db-hsql-dity1";
	final static String urlMySqlDb = "jdbc:mysql://localhost/hol?useUnicode=true&characterEncoding=utf-8";
	//development server /home/roman/algoritmed.com/h2-server
//	final static String urlH2Db = "jdbc:h2:tcp://localhost/db-hol2-eih/db-hol2-eih";
//	final static String urlK1Db = "jdbc:hsqldb:hsql://localhost/db-hsql-dity1";
//	final static String urlMySqlDb = "jdbc:mysql://localhost/hol?useUnicode=true&characterEncoding=utf-8";
	//product server /home/hol2/db-h2
//	final static String urlH2Db = "jdbc:h2:tcp://localhost/db-hol2-eih/db-hol2-eih";
//	final static String urlK1Db = "jdbc:hsqldb:hsql://localhost/db-hsql-dity1";
//	final static String urlMySqlDb = "jdbc:mysql://localhost/hol?useUnicode=true&characterEncoding=utf-8";
	//-----------forAll--------------------END


//	final static String applicationFolderPfad = "/home/roman/algoritmed.com/dev-workspace-20160406/hol2eih4/";
//	final static String urlH2Db = "jdbc:h2:/home/roman/db-java/h2-server/db-hol2-eih/db-hol2-eih";
	//all
	private static Integer workYear = DateTime.now().getYear();
	public static void setWorkYear(Integer workYear) {
		AppConfig.workYear = workYear;
	}
	private final static String excelFileName = "pyx-2015-v.2.xls";
	public static String getExcelfilename() {
		String excelWorkFileName = excelFileName.replace("2015", workYear.toString());
		return excelWorkFileName;
	}
	final static String applicationResourcesFolderPfad	= applicationFolderPfad+"src/main/resources/";
	final static String innerExcelFolderPfad	= applicationFolderPfad + "src/main/webapp/excel/";

}
