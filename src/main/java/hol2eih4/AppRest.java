package hol2eih4;

import java.security.Principal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
		List<Map<String, Object>> moveTodayPatientsList = appService.readMoveTodayPatients();
		moveTodayPatients.put("moveTodayPatientsList", moveTodayPatientsList);
		moveTodayPatients.put("today", new Date());
		return moveTodayPatients;
	}
	// 1.2   Запис надходження/виписки хворих на сьогодні – saveMoveTodayPatients
	@RequestMapping(value = "/saveMoveTodayPatients", method = RequestMethod.POST)
	public  @ResponseBody Map<String, Object> saveMoveTodayPatients(@RequestBody Map<String, Object> moveTodayPatients, Principal userPrincipal) {
		logger.info("\n Start /saveMoveTodayPatients");
		
		appService.saveMoveTodayPatients(moveTodayPatients);
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
