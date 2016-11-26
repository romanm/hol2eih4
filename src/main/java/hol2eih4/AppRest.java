package hol2eih4;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class AppRest {
	private static final Logger logger = LoggerFactory.getLogger(AppRest.class);
	
	@Autowired private NamedParameterJdbcTemplate hol1EihParamJdbcTemplate;
//	@Autowired private Hol1DbService hol1DbService;
	@Autowired private AppService appService;
	@Autowired private ExcelService2 excelService;
	//  1  Запис надходжень/виписки хворих за сьогодні – saveMovePatients.html.
	//  1.1  Зчитування надходження/виписки хворих на сьогодні – readTodayMovePatients
	
	@GetMapping("/v/readHome")
	public  @ResponseBody Map<String, Object> readHome(Principal principal, HttpServletRequest request) {
		logger.info("\n ------------------------- Start /readHome");
		Map<String, Object> model = new HashMap<String, Object>();
		DateTime today = new DateTime();
		model.put("today", today.toDate());
		model.put("principal", principal);
		return model;
	}
	@RequestMapping(value = "/session-year/{yyyy}", method = RequestMethod.GET)
	public String readMoveyyyymmddPatients(
			@PathVariable Integer yyyy, HttpServletRequest request ) {
		logger.debug(""+yyyy);
		setSessionYear(yyyy, request);
		return "redirect:/";
	}
	private void setSessionYear(Integer yyyy, HttpServletRequest request) {
		request.getSession().setAttribute("year", yyyy);
		AppConfig.setWorkYear(yyyy);
	}
	
	@RequestMapping(value = "/r/readBedDayH2-{m1}-{m2}", method = RequestMethod.GET)
	public  @ResponseBody Map<String, Object> readBedDayOfMonthH2(
			@PathVariable Integer m1
			,@PathVariable Integer m2
			,Principal userPrincipal) {
		logger.debug("---------------- 1");
		Map<String, Object> map = new HashMap<>();
		map.put("hello", "World");
		map.put("m1", m1);
		map.put("m2", m2);
		logger.debug("---------------- 2");
		List<Map<String, Object>> bedDayOfMonthH2 = appService.readBedDayOfMonthH2(m1,m2);
		map.put("bedDayOfMonthH2", bedDayOfMonthH2);
		return map;
	}

	

	@GetMapping("/r/readBedDayDepartmentMySql-{m1}-{m2}-{year}")
	public  @ResponseBody Map<String, Object> readBedDayDepartmentMySql(
			@PathVariable Integer m1
			,@PathVariable Integer m2
			,@PathVariable Integer year
			,Principal userPrincipal) {
		StopWatch watch = new StopWatch();
		watch.start();
		Map<String, Object> map = new HashMap<>();
		map.put("m1", m1);
		map.put("m2", m2);
		List<Map<String, Object>> bedDayOfMonthMySql = appService.readBedDayOfMonthMySql(m1,m2,year);
		map.put("bedDayOfMonthMySql", bedDayOfMonthMySql);
		if(m1 < m2) {
			List<Object> arrayList = new ArrayList<>();
			for (int i = m1; i <= m2; i++) {
				List<Map<String, Object>> readBedDayOfMonthMySql = appService.readBedDayOfMonthMySql(i,i,year);
				arrayList.add(readBedDayOfMonthMySql);
			}
			map.put("bedDayDepartmentMySql", arrayList);
		}
		watch.stop();
		map.put("duration", watch.getTotalTimeSeconds());
		System.out.println("duration = " + map.get("duration"));
		return map;
	}


	@RequestMapping(value = "/r/readIcd10K1-{m1}-{m2}", method = RequestMethod.GET)
	public  @ResponseBody Map<String, Object> readIcd10K1(
			@PathVariable Integer m1
			,@PathVariable Integer m2
			,Principal userPrincipal) {
		Map<String, Object> map = new HashMap<>();
		map.put("m1", m1);
		map.put("m2", m2);
		List<Map<String, Object>> icd10K1 = appService.readIcd10K1(m1,m2);
		map.put("icd10K1", icd10K1);
		if(m1 < m2) {
			List<Object> someMonth = new ArrayList<>();
			for (int i = m1; i <= m2; i++) {
				List<Map<String, Object>> icd10K1Month1 = appService.readIcd10K1(i,i);
				someMonth.add(icd10K1Month1);
			}
			map.put("someMonth", someMonth);
		}
		return map;
	}


	private @Value("${sql.hol1Eih.departmentMotion}") String departmentMotion;
	@GetMapping("/r/readDepartmentMotion-{year}-{m1}-{m2}-{departmentId}")
	public  @ResponseBody Map<String, Object> readDepartmentMotion(
			@PathVariable Integer year
			,@PathVariable Integer m1
			,@PathVariable Integer m2
			,@PathVariable Integer departmentId
			,Principal userPrincipal) {
		StopWatch watch = new StopWatch();
		watch.start();
		Map<String, Object> map = new HashMap<>();
		map.put("year", year);
		map.put("min_month", m1);
		map.put("max_month", m2);
		map.put("department_id", departmentId);
		logger.info("/r/readDepartmentMotion-{year}-{m1}-{m2}-{departmentId} \n"+ map);
		List<Map<String, Object>> bedDayOfMonthMySql
			= hol1EihParamJdbcTemplate.queryForList(departmentMotion, map);
//		List<Map<String, Object>> bedDayOfMonthMySql = appService.readDepartmentMotion(m1,m2, departmentId);
		map.put("bedDayOfMonthMySql", bedDayOfMonthMySql);
		Map<String, Object> department = appService.readDepartment(departmentId);
		map.put("department", department);
		map.put("departmentId", departmentId);
		map.put("m1", m1);
		map.put("m2", m2);
		watch.stop();
		map.put("duration", watch.getTotalTimeSeconds());
		System.out.println("duration = " + map.get("duration"));
		return map;
	}

	private @Value("${sql.hol1Eih.departmentAdress}") String departmentAdress;
	@GetMapping("/r/readDepartmentAdress-{year}-{m1}-{m2}-{departmentId}")
	public  @ResponseBody Map<String, Object> readDepartmentAdress(
			@PathVariable Integer year
			,@PathVariable Integer m1
			,@PathVariable Integer m2
			,@PathVariable Integer departmentId
			,Principal userPrincipal) {
		StopWatch watch = new StopWatch();
		watch.start();
		Map<String, Object> map = new HashMap<>();
		map.put("year", year);
		map.put("min_month", m1);
		map.put("max_month", m2);
		map.put("department_id", departmentId);
		logger.info("/r/readDepartmentAdress-{year}-{m1}-{m2}-{departmentId} \n " + map);
		System.out.println(departmentAdress.replace(":year", ""+year)
				.replaceAll(":min_month", ""+m1)
				.replaceAll(":max_month", ""+m2)
				.replaceAll(":department_id", ""+departmentId)
				);
		List<Map<String, Object>> bedDayOfMonthMySql
			= hol1EihParamJdbcTemplate.queryForList(departmentAdress, map);
//		List<Map<String, Object>> bedDayOfMonthMySql = appService.readDepartmentAdress(m1,m2, departmentId);
		map.put("bedDayOfMonthMySql", bedDayOfMonthMySql);
		Map<String, Object> department = appService.readDepartment(departmentId);
		map.put("department", department);
		watch.stop();
		map.put("duration", watch.getTotalTimeSeconds());
		System.out.println("duration = " + map.get("duration"));
		map.put("departmentId", departmentId);
		map.put("m1", m1);
		map.put("m2", m2);
		return map;
	}

	private @Value("${sql.hol1Eih.departmentIcd10Group}") String departmentIcd10Group;
	@GetMapping("/r/readDepartmentIcd10Group-{year}-{m1}-{m2}-{departmentId}")
	public  @ResponseBody Map<String, Object> readDepartmentIcd10Group(
			@PathVariable Integer year
			,@PathVariable Integer m1
			,@PathVariable Integer m2
			,@PathVariable Integer departmentId
			,Principal userPrincipal) {
		Map<String, Object> map = new HashMap<>();
		StopWatch watch = new StopWatch();
		watch.start();
		map.put("year", year);
		map.put("min_month", m1);
		map.put("max_month", m2);
		map.put("department_id", departmentId);
		logger.info("/r/readDepartmentAdress-{year}-{m1}-{m2}-{departmentId} \n " + map);
		List<Map<String, Object>> bedDayOfMonthMySql 
		= hol1EihParamJdbcTemplate.queryForList(departmentIcd10Group, map);
//		List<Map<String, Object>> bedDayOfMonthMySql = appService.readDepartmentIcd10Group(m1,m2, departmentId);
		map.put("bedDayOfMonthMySql", bedDayOfMonthMySql);
		Map<String, Object> department = appService.readDepartment(departmentId);
		map.put("department", department);
		watch.stop();
		map.put("duration", watch.getTotalTimeSeconds());
		System.out.println("duration = " + map.get("duration"));
		map.put("departmentId", departmentId);
		map.put("m1", m1);
		map.put("m2", m2);
		return map;
	}

	private @Value("${sql.hol1Eih.departmentIcd10}") String departmentIcd10;
	@GetMapping("/r/readDepartmentIcd10-{year}-{m1}-{m2}-{departmentId}")
	public  @ResponseBody Map<String, Object> readDepartmentIcd10(
			@PathVariable Integer year
			,@PathVariable Integer m1
			,@PathVariable Integer m2
			,@PathVariable Integer departmentId
			,Principal userPrincipal) {
		Map<String, Object> map = new HashMap<>();
		StopWatch watch = new StopWatch();
		watch.start();
		map.put("year", year);
		map.put("min_month", m1);
		map.put("max_month", m2);
		map.put("department_id", departmentId);
		logger.info("/r/readDepartmentAdress-{year}-{m1}-{m2}-{departmentId} \n " + map);
		List<Map<String, Object>> bedDayOfMonthMySql 
		= hol1EihParamJdbcTemplate.queryForList(departmentIcd10, map);
//		List<Map<String, Object>> bedDayOfMonthMySql = appService.readDepartmentIcd10(m1,m2, departmentId);
		map.put("bedDayOfMonthMySql", bedDayOfMonthMySql);
		Map<String, Object> department = appService.readDepartment(departmentId);
		map.put("department", department);
		watch.stop();
		map.put("duration", watch.getTotalTimeSeconds());
		System.out.println("duration = " + map.get("duration"));
		map.put("departmentId", departmentId);
		map.put("m1", m1);
		map.put("m2", m2);
		return map;
	}

	@RequestMapping(value = "/r/readBedDayReception-{mm}", method = RequestMethod.GET)
	public  @ResponseBody Map<String, Object> readBedDayReception(
			@PathVariable Integer mm
			,Principal userPrincipal) {
		logger.debug("---------------- 1");
		Map<String, Object> map = new HashMap<>();
		map.put("hello", "World");
		map.put("mm", mm);
		logger.debug("---------------- 2");
		return map;
	}

	// 2  Показ кількості надходжень/виписки хворих за останні 7 днів – movePatients.html.
	// 2.1  Зчитування руху хворих за останні 7 днів – readMovePatients
	@RequestMapping(value = "/readMovePatients", method = RequestMethod.GET)
	public  @ResponseBody Map<String, Object> readMovePatients( Principal userPrincipal) {
		logger.info("\n Start /readMovePatients");
		Map<String, Object> readMovePatients = appService.readMovePatients();
		return readMovePatients;
	}

	//  1.1  Зчитування надходження/виписки хворих на дату – readTodayMovePatients
	

	// 1.2   Запис надходження/виписки хворих на сьогодні – saveMoveTodayPatients
	@RequestMapping(value = "/saveMoveTodayPatients", method = RequestMethod.POST)
	public  @ResponseBody Map<String, Object> saveMoveTodayPatients(@RequestBody Map<String, Object> moveTodayPatients, Principal userPrincipal) {
		logger.info("\n Start /saveMoveTodayPatients");
		appService.saveMoveTodayPatients(moveTodayPatients, new DateTime());
		String url = AppConfig.hol2webHost
				+ "/saveMoveTodayPatients";
		saveToHolWeb(moveTodayPatients, url);
		return moveTodayPatients;
	}

	// 1.3   Запис надходження/виписки хворих на дату – saveMoveyyyymmddPatients
	@RequestMapping(value = "/save-{yyyy}-{mm}-{dd}-Patients", method = RequestMethod.POST)
	public  @ResponseBody Map<String, Object> save_yyyymmdd_Patients(
			@PathVariable Integer yyyy , @PathVariable Integer mm, @PathVariable Integer dd
			,@RequestBody Map<String, Object> moveTodayPatients, Principal userPrincipal) {
		DateTime dateTime = new DateTime(yyyy,mm,dd,0,0);
		logger.info("\n Start /save_yyyymmdd_Patients"+dateTime);
		moveTodayPatients.put("today",dateTime.getMillis());
		appService.saveMoveTodayPatients(moveTodayPatients,dateTime);
		String url = AppConfig.hol2webHost
				+ "/save-"
				+ AppConfig.yyyyMMddDateFormat.format(dateTime.toDate())
				+ "-Patients";
//		saveToHolWeb(moveTodayPatients, url);
		return moveTodayPatients;
	}

	@RequestMapping(value = "/init-excel", method = RequestMethod.GET)
	public void initExcell(HttpServletResponse response){
		try {
			excelService.initExcel(response);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/file-excel-{yyyy}", method = RequestMethod.GET)
	public String getExcell(@PathVariable Integer yyyy){
		logger.debug(yyyy+"/");
		String sourceFile = AppConfig.applicationExcelFolderPfad+AppConfig.getExcelfilename();
		String targetFile = AppConfig.innerExcelFolderPfad+AppConfig.getExcelfilename();
		logger.debug("cp "+sourceFile+" "+targetFile);
		excelService.copyFile(sourceFile, targetFile);
		return "redirect:/excel/pyx-2016-v.2.xls";
	}

	@RequestMapping(value = "/create-read-{yyyy}-{mm}-{dd}-excel", method = RequestMethod.GET)
	public String createReadExcell(
			@PathVariable Integer yyyy , @PathVariable Integer mm, @PathVariable Integer dd,
			Principal userPrincipal, HttpServletRequest request) {
		logger.debug("/create-read-{yyyy}-{mm}-{dd}-excel");
		DateTime dateTime = new DateTime(yyyy,mm,dd,0,0);
		excelService.createExcel(dateTime);
		return "redirect:excel/" + AppConfig.getExcelfilename(); 
	}
	private void saveToHolWeb(Map<String, Object> moveTodayPatients, String url) {
		
//		Map<String, Object> moveTodayPatients2 = copyPart(moveTodayPatients);
		
		HttpURLConnection postToUrl = postToUrl(moveTodayPatients,url);
		logger.debug(""+postToUrl);
		
		try {
			InputStream requestBody = postToUrl.getInputStream();
			logger.debug(""+requestBody);
			Map readValue = mapper.readValue(requestBody, Map.class);
			logger.debug(""+readValue);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private Map<String, Object> copyPart(Map<String, Object> moveTodayPatients) {
		Map<String, Object> moveTodayPatients2 = new HashMap<String,Object>();
		Set<String> keySet = moveTodayPatients.keySet();
		for (String string : keySet) {
			Object object = moveTodayPatients.get(string);
			if(string.equals("moveTodayPatientsList"))
			{
				List l2 = new ArrayList<>();
				List l = (List) object;
				int size = l.size();
				logger.debug(""+size);
				for (int i = 0; i < 12; i++) {
					Object object2 = l.get(i);
					l2.add(object2);
				}
				moveTodayPatients2.put(string, l2);
			}else{
				moveTodayPatients2.put(string, object);
			}
		}
		logger.debug(moveTodayPatients2.toString());
		return moveTodayPatients2;
	}
		// 3  План операцій – operationsPlan.html
	// 3.1  Запис плану операцій на завтра – saveTomorrowOperationsPlan
	@RequestMapping(value = "/saveTomorrowOperationsPlan", method = RequestMethod.POST)
	public  @ResponseBody Map<String, Object> saveTomorrowOperationsPlan(@RequestBody Map<String, Object> tomorrowOperationsPlan, Principal userPrincipal) {
		logger.info("\n Start /saveTomorrowOperationsPlan");
		appService.saveTomorrowOperationsPlan(tomorrowOperationsPlan);
		return tomorrowOperationsPlan;
	}
	// 3.2  Зчитування плану операцій на сьогодні – readTodayOperationsPlan
	@RequestMapping(value = "/readTodayOperationsPlan", method = RequestMethod.GET)
	public  @ResponseBody Map<String, Object> readTodayOperationsPlan(@RequestBody Map<String, Object> copeTodayPatients, Principal userPrincipal) {
		logger.info("\n Start /readTodayOperationsPlan");
		Map<String, Object> todayOperationsPlan = appService.readTodayOperationsPlan();
		return todayOperationsPlan;
	}
	// 4  Список операцій відділення по датах –  departmentOperationsList.html
	// 4.1  Зчитування списку операцій за останні 7 днів –  readListOperationsPlan
	@RequestMapping(value = "/readListOperationsPlan", method = RequestMethod.GET)
	public  @ResponseBody Map<String, Object> readListOperationsPlan( Principal userPrincipal) {
		logger.info("\n Start /readListOperationsPlan");
		Map<String, Object> readListOperationsPlan = appService.readListOperationsPlan();
		return readListOperationsPlan;
	}
	
	private Map<String, Object> getMapFromUrl(Map<String, Object> mapObject, String urlStr) {
		try {
			URL url = new URL(urlStr);
			logger.debug(""+url);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			logger.debug(""+con);
			con.setRequestMethod("GET");
			con.setDoOutput(true);
			con.setRequestProperty("Content-Type", "application/json"); 
			con.setRequestProperty("charset", "utf-8");
			mapper.writeValue(con.getOutputStream(), mapObject);
			InputStream requestBody = con.getInputStream();
			logger.debug(""+requestBody);
			Map readValue = mapper.readValue(requestBody, Map.class);
			logger.debug(""+readValue);
			return readValue;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private HttpURLConnection postToUrl(Map<String, Object> mapObject, String url) {
		logger.debug(url);
		try {
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			logger.debug(""+con);
			con.setRequestMethod("POST");
			con.setDoOutput(true);
			con.setRequestProperty("Content-Type", "application/json"); 
			con.setRequestProperty("charset", "utf-8");
			logger.debug(" mapObject = " + mapObject);
			mapper.writeValue(con.getOutputStream(), mapObject);
			logger.debug("2");
			return con;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e1) {
			e1.printStackTrace();
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	ObjectMapper mapper = new ObjectMapper();
}
