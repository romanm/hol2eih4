package hol2eih4.eix;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import hol2eih4.AppConfig;
import hol2eih4.AppService;

@Controller
public class EixTmpRest {
	private static final Logger logger = LoggerFactory.getLogger(EixTmpRest.class);
	@Autowired private AppService appService;

	@RequestMapping(value = "/readMove-{yyyy}-{mm}-{dd}-Patients", method = RequestMethod.GET)
	public  @ResponseBody Map<String, Object> readMoveyyyymmddPatients(
			@PathVariable Integer yyyy , @PathVariable Integer mm, @PathVariable Integer dd
			,Principal principal) {
		Map<String, Object> moveTodayPatients = new HashMap<String, Object>();
		DateTime dateTime = new DateTime(yyyy,mm,dd,0,0);
		logger.info("\n Start /readMove-"
				+ AppConfig.yyyyMMddDateFormat.format(dateTime.toDate())
				+ "-Patients ("+dateTime+") ");
		List<Map<String, Object>> moveTodayPatientsList = appService.readMoveTodayPatients(dateTime);
		moveTodayPatients.put("moveTodayPatientsList", moveTodayPatientsList);
		moveTodayPatients.put("today", dateTime.toDate());
		moveTodayPatients.put("principal", principal);
		return moveTodayPatients;
	}

	@GetMapping("/r/readBedDayMySql-{m1}-{m2}-{year}")
	public  @ResponseBody Map<String, Object> readBedDayMySql(
			 @PathVariable Integer m1
			 ,@PathVariable Integer m2
			 ,@PathVariable Integer year
			,Principal userPrincipal) {
		Map<String, Object> map = new HashMap<>();
		map.put("m1", m1);
		map.put("m2", m2);
		StopWatch watch = new StopWatch();
		watch.start();
		List<Map<String, Object>> bedDayOfMonthMySql = appService.readBedDayOfMonthMySql(m1,m2,year);
		watch.stop();
		map.put("duration", watch.getTotalTimeSeconds());
		System.out.println("duration = " + map.get("duration"));
		map.put("bedDayOfMonthMySql", bedDayOfMonthMySql);
		return map;
	}

	@RequestMapping(value = "/readMoveTodayPatients", method = RequestMethod.GET)
	public  @ResponseBody Map<String, Object> readMoveTodayPatients(Principal principal, HttpServletRequest request) {
		logger.info("\n ------------------------- Start /readMoveTodayPatients");
		DateTime today = new DateTime();
		Integer sessionYear = (Integer) request.getSession().getAttribute("year");
		if(sessionYear != null)
		{
			today = today.withYear(sessionYear);
		}
		List<Map<String, Object>> moveTodayPatientsList = appService.readMoveTodayPatients(today);
		Map<String, Object> moveTodayPatients = new HashMap<String, Object>();
		moveTodayPatients.put("moveTodayPatientsList", moveTodayPatientsList);
		moveTodayPatients.put("today", today.toDate());
		moveTodayPatients.put("principal", principal);
		return moveTodayPatients;
	}

}
