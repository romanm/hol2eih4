package hol2eih4.eix;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static java.lang.Math.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class EixRest {
	private static final Logger logger = LoggerFactory.getLogger(EixRest.class);
	@Autowired NamedParameterJdbcTemplate hol1EihParamJdbcTemplate;
	@Autowired JdbcTemplate hol1EihJdbcTemplate;

	@Value("${sql.hol1.ruh.f007s1.dayDepartmentPatients}") private String sqlHol1RuhF007s1dayDepartmentPatients;
	@Value("${sql.hol1.ruh.f007s2.dayDepartmentPatients}") private String sqlHol1RuhF007s2dayDepartmentPatients;

	@GetMapping(value = "/v/department-{departmentId}-PatientsMove-{yyyy}-{mm}-{dd}")
	public @ResponseBody Map<String, Object> department_departmentId_PatientsMove_yyyymmdd(
			@PathVariable Integer departmentId ,
			@PathVariable Integer yyyy ,
			@PathVariable Integer mm, @PathVariable Integer dd
			,Principal principal) {
		Map<String, Object> map =  new HashMap<>();
		map.put("departmentId", departmentId);
		map.put("yyyy", yyyy);
		map.put("mm", mm);
		map.put("dd", dd);
		logger.info("\n ------ " + "/v/department-{departmentId}-PatientsMove-{yyyy}-{mm}-{dd}"
				+ "\n" + map 
				+ "\n" + sqlHol1RuhF007s1dayDepartmentPatients
				.replace(":departmentId", ""+departmentId)
				.replace(":yyyy", ""+yyyy)
				.replace(":mm", ""+mm).replace(":dd", ""+dd)
				+ "\n" + sqlHol1RuhF007s2dayDepartmentPatients
				.replace(":departmentId", ""+departmentId)
				.replace(":yyyy", ""+yyyy)
				.replace(":mm", ""+mm).replace(":dd", ""+dd));
		StopWatch watch = new StopWatch();
		watch.start();
		Map<String, Object> mapDayRuhF007s1InDepartment 
		= hol1EihParamJdbcTemplate.queryForMap(sqlHol1RuhF007s1dayDepartmentPatients, map);
		map.put("mapDayRuhF007s1InDepartment", mapDayRuhF007s1InDepartment);
		List<Map<String, Object>> listDayRuhF007s2InDepartment 
		= hol1EihParamJdbcTemplate.queryForList(sqlHol1RuhF007s2dayDepartmentPatients, map);
		map.put("listDayRuhF007s2InDepartment", listDayRuhF007s2InDepartment);
		map.put("principal", principal);
		watch.stop();
		map.put("duration", watch.getTotalTimeSeconds());
		System.out.println("duration = " + map.get("duration"));
		return map;
	}

	@Value("${sql.hol1.f007s1.dayDepartment}") private String sqlHol1F007s1DayDepartment;
	@Value("${sql.hol1.f007s2.dayDepartment}") private String sqlHol1F007s2DayDepartment;
	@GetMapping(value = "/v/hol1F007Day-{yyyy}-{mm}-{dd}-department-{departmentId}")
	public @ResponseBody Map<String, Object> hol1F007Day_yyyymmdd_department_departmentId(
			@PathVariable Integer departmentId ,
			@PathVariable Integer yyyy ,
			@PathVariable Integer mm, @PathVariable Integer dd
			,Principal principal) {
		StopWatch watch = new StopWatch();
		watch.start();
		Map<String, Object> map =  new HashMap<>();
		map.put("departmentId", departmentId);
		map.put("yyyy", yyyy);
		map.put("mm", mm);
		map.put("dd", dd);
		logger.info("\n ------ " + "/v/department-{departmentId}-PatientsMove-{yyyy}-{mm}-{dd}"
				+ "\n" + map 
				+ "\n" + sqlHol1F007s1DayDepartment
				.replace(":departmentId", ""+departmentId)
				.replace(":yyyy", ""+yyyy)
				.replace(":mm", ""+mm).replace(":dd", ""+dd)
				+ "\n" + sqlHol1F007s2DayDepartment
				.replace(":departmentId", ""+departmentId)
				.replace(":yyyy", ""+yyyy)
				.replace(":mm", ""+mm).replace(":dd", ""+dd));
		Map<String, Object> mapHol1F007s1DayDepartment 
		= hol1EihParamJdbcTemplate.queryForMap(sqlHol1F007s1DayDepartment, map);
		map.put("mapHol1F007s1DayDepartment", mapHol1F007s1DayDepartment);
		List<Map<String, Object>> listHol1F007s2DayDepartment 
		= hol1EihParamJdbcTemplate.queryForList(sqlHol1F007s2DayDepartment, map);
		map.put("listHol1F007s2DayDepartment", listHol1F007s2DayDepartment);
		map.put("principal", principal);
		watch.stop();
		map.put("duration", watch.getTotalTimeSeconds());
		System.out.println("duration = " + map.get("duration"));
		return map;
	}

	@Value("${sql.hol1.f007.spital}") private String sqlHol1F007Spital;
	@GetMapping(value = "/v/hol1F007Spital-{yyyy}-{mm}-{dd}")
	public  @ResponseBody Map<String, Object> hol1F007Spital_yyyymmdd(
			@PathVariable Integer yyyy , @PathVariable Integer mm, @PathVariable Integer dd
			,Principal principal) {
		StopWatch watch = new StopWatch();
		watch.start();
		Map<String, Object> map =  new HashMap<>();
		map.put("yyyy", yyyy);
		map.put("mm", mm);
		map.put("dd", dd);
		logger.info("\n ------ " + "/v/departmentPatientsMove-{yyyy}-{mm}-{dd}"
				+ "\n" + map 
				+ "\n" + sqlHol1F007Spital.replace(":yyyy", ""+yyyy).replace(":mm", ""+mm).replace(":dd", ""+dd)
				);
		List<Map<String, Object>> hol1F007Spital 
		= hol1EihParamJdbcTemplate.queryForList(sqlHol1F007Spital, map);
		map.put("hol1F007Spital", hol1F007Spital);
		watch.stop();
		map.put("duration", watch.getTotalTimeSeconds());
		System.out.println("duration = " + map.get("duration"));
		return map;
	}

	@Value("${sql.hol1.ruh.f007.departmentHistoryForDay}") private String sqlHol1RuhF007DepartmentHistoryForDay;
	@Value("${sql.hol1.ruh.f007Spital}") private String sqlHol1RuhF007Spital;
	@GetMapping(value = "/v/departmentPatientsMove-{yyyy}-{mm}-{dd}")
	public  @ResponseBody Map<String, Object> departmentPatientsMove_yyyymmdd(
			@PathVariable Integer yyyy , @PathVariable Integer mm, @PathVariable Integer dd
			,Principal principal) {
		Map<String, Object> map =  new HashMap<>();
		map.put("yyyy", yyyy);
		map.put("mm", mm);
		map.put("dd", dd);
		logger.info("\n ------ " + "/v/departmentPatientsMove-{yyyy}-{mm}-{dd}\n" + map 
		+ "\n" + sqlHol1RuhF007DepartmentHistoryForDay.replace(":yyyy", ""+yyyy).replace(":mm", ""+mm).replace(":dd", ""+dd)
		+ "\n" + sqlHol1RuhF007Spital.replace(":yyyy", ""+yyyy).replace(":mm", ""+mm).replace(":dd", ""+dd)
		);
		StopWatch watch = new StopWatch();
		watch.start();
		List<Map<String, Object>> listDayRuhInSpital 
			= hol1EihParamJdbcTemplate.queryForList(sqlHol1RuhF007Spital, map);
		map.put("listDayRuhInSpital", listDayRuhInSpital);
		watch.stop();
		map.put("duration", watch.getTotalTimeSeconds());
		System.out.println("duration = " + map.get("duration"));
		return map;
	}

	@Value("${sql.hol1.bed_day_all_department}") private String sqlHol1BedDayAllDepartment;
	@GetMapping("/r/readBedDayAllDepartment-{m1}-{m2}-{year}")
	public  @ResponseBody Map<String, Object> readBedDayAllDepartment(
			 @PathVariable Integer m1
			 ,@PathVariable Integer m2
			 ,@PathVariable Integer year
			,Principal userPrincipal) {
		Map<String, Object> map = new HashMap<>();
		map.put("min_month", min(m1, m2));
		map.put("max_month", max(m1, m2));
		map.put("year", year);
		StopWatch watch = new StopWatch();
		watch.start();
		List<Map<String, Object>> bedDayAllDepartment
			= hol1EihParamJdbcTemplate.queryForList(sqlHol1BedDayAllDepartment, map);
		watch.stop();
		map.put("duration", watch.getTotalTimeSeconds());
		System.out.println("duration = " + map.get("duration"));
		map.put("bedDayAllDepartment", bedDayAllDepartment);
		return map;
	}
	
	
	@Value("${sql.hol1.eix.historyPatient}") private String sqlHol1EixHistoryPatient;
	@Value("${sql.hol1.eix.history_diagnose}") private String sqlHol1EixHistoryDiagnose;
	@Value("${sql.hol1.eix.department_history}") private String sqlHol1EixDepartmentHistory;
	@Value("${sql.hol1.eix.operation_history}") private String sqlHol1EixOperationHistory;
	@Value("${sql.hol1.eix.history_treatment_analysis}") private String sqlHol1EixHistoryTreatmentAnalysis;
	@GetMapping("/r/eix-{eixId}")
	public  @ResponseBody Map<String, Object> readEix(
			@PathVariable Integer eixId
			,Principal userPrincipal) {
		StopWatch watch = new StopWatch();
		watch.start();
		
		Map<String, Object> map = new HashMap<>();
		map.put("eixId", eixId);
		logger.info(" ------------------------- \n"
				+ "/r/eix-{eixId}"+ map);
		Map<String, Object> historyPatient = 
				hol1EihParamJdbcTemplate.queryForMap(sqlHol1EixHistoryPatient, map);
		map.put("historyPatient", historyPatient);
		List<Map<String, Object>> historyDiagnose = 
				hol1EihParamJdbcTemplate.queryForList(sqlHol1EixHistoryDiagnose, map);
		map.put("historyDiagnose", historyDiagnose);
		List<Map<String, Object>> departmentHistory = 
				hol1EihParamJdbcTemplate.queryForList(sqlHol1EixDepartmentHistory, map);
		map.put("departmentHistory", departmentHistory);
		List<Map<String, Object>> operationHistory = 
				hol1EihParamJdbcTemplate.queryForList(sqlHol1EixOperationHistory, map);
		map.put("operationHistory", operationHistory);
		List<Map<String, Object>> historyTreatmentAnalysis = 
				hol1EihParamJdbcTemplate.queryForList(sqlHol1EixHistoryTreatmentAnalysis, map);
		map.put("historyTreatmentAnalysis", historyTreatmentAnalysis);
		
		watch.stop();
		map.put("duration", watch.getTotalTimeSeconds());
		System.out.println("duration = " + map.get("duration"));
		
		return map;
	}
	
	@RequestMapping("/v/eix/{id}")
	public String ixWithId(@PathVariable Integer id, Model model) {
		logger.info("------------------------- \n"
				+ " /v/eix/"+id);
		model.addAttribute("quotes", "'");
		model.addAttribute("ng_template", "viewEix");
//		model.addAttribute("ng_controller", "Eix2Ctrl as eixCtrl");
		model.addAttribute("ng_controller", "Ix3Ctrl as ix3Ctrl");
		model.addAttribute("ix", id);
		System.out.println(model);
		return "ix3";
	}

	@Value("${sql.hol1.countries}") private String sqlHol1EihCountries;
	@GetMapping("/v/eix/test")
	public @ResponseBody Map<String, Object> ixWithId() {
		logger.info("\n ------ /v/eix/test");
		Map<String, Object> map =  new HashMap<>();
		map.put("test", "eix");
		List<Map<String, Object>> listCountries = hol1EihJdbcTemplate.queryForList(sqlHol1EihCountries);
		map.put("listCountries", listCountries);
		List<Map<String, Object>> listBedDay = hol1EihJdbcTemplate.queryForList(sqlHol1BedDayAllDepartment);
		map.put("listBedDay", listBedDay);
		return map;
	}

}
