package hol2eih4;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AppRest {
	private static final Logger logger = LoggerFactory.getLogger(AppRest.class);
//	@Autowired private Hol1DbService hol1DbService;
	@Autowired private AppService appService;
	//  1  Запис надходжень/виписки хворих за сьогодні – saveMovePatients.html.
	//  1.1  Зчитування надходження/виписки хворих на сьогодні – readTodayMovePatients
	@RequestMapping(value = "/readMoveTodayPatients", method = RequestMethod.GET)
	public  @ResponseBody Map<String, Object> readMoveTodayPatients(Principal userPrincipal) {
		logger.info("\n Start /readMoveTodayPatients");
		Map<String, Object> moveTodayPatients = new HashMap<String, Object>();
		DateTime today = new DateTime();
		List<Map<String, Object>> moveTodayPatientsList = appService.readMoveTodayPatients(today);
		moveTodayPatients.put("moveTodayPatientsList", moveTodayPatientsList);
		moveTodayPatients.put("today", new DateTime().toDate());
		return moveTodayPatients;
	}
	//  1.1  Зчитування надходження/виписки хворих на дату – readTodayMovePatients
	@RequestMapping(value = "/readMove-{yyyy}-{mm}-{dd}-Patients", method = RequestMethod.GET)
	public  @ResponseBody Map<String, Object> readMoveyyyymmddPatients(
			@PathVariable Integer yyyy , @PathVariable Integer mm, @PathVariable Integer dd
			,Principal userPrincipal) {
		Map<String, Object> moveTodayPatients = new HashMap<String, Object>();
		DateTime dateTime = new DateTime(yyyy,mm,dd,0,0);
		logger.info("\n Start /readMove-"
				+ AppConfig.yyyyMMddDateFormat.format(dateTime.toDate())
				+ "-Patients ("+dateTime+") ");
		List<Map<String, Object>> moveTodayPatientsList = appService.readMoveTodayPatients(dateTime);
		moveTodayPatients.put("moveTodayPatientsList", moveTodayPatientsList);
		moveTodayPatients.put("today", dateTime.toDate());
		return moveTodayPatients;
	}
	// 1.2   Запис надходження/виписки хворих на сьогодні – saveMoveTodayPatients
	@RequestMapping(value = "/saveMoveTodayPatients", method = RequestMethod.POST)
	public  @ResponseBody Map<String, Object> saveMoveTodayPatients(@RequestBody Map<String, Object> moveTodayPatients, Principal userPrincipal) {
		logger.info("\n Start /saveMoveTodayPatients");
		appService.saveMoveTodayPatients(moveTodayPatients, new DateTime());
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
		return moveTodayPatients;
	}
	// 2  Показ кількості надходжень/виписки хворих за останні 7 днів – movePatients.html.
	// 2.1  Зчитування руху хворих за останні 7 днів – readMovePatients
	@RequestMapping(value = "/readMovePatients", method = RequestMethod.GET)
	public  @ResponseBody Map<String, Object> readMovePatients( Principal userPrincipal) {
		logger.info("\n Start /readMovePatients");
		Map<String, Object> readMovePatients = appService.readMovePatients();
		return readMovePatients;
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
}
