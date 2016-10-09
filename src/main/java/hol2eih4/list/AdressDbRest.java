package hol2eih4.list;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AdressDbRest {
	private static final Logger logger = LoggerFactory.getLogger(AdressDbRest.class);
	@Autowired NamedParameterJdbcTemplate hol1EihParamJdbcTemplate;
	@Autowired JdbcTemplate hol1EihJdbcTemplate;

	private @Value("${sql.hol1.cnt.patient.of.country}")	String sqlHol1CntPatientOfCountry;
	private @Value("${sql.hol1.cnt.patient.of.district}")	String sqlHol1CntPatientOfDistrict;
	private @Value("${sql.hol1.cnt.patient.of.region}")		String sqlHol1CntPatientOfRegion;
	private @Value("${sql.hol1.cnt.patient.of.locality}")	String sqlHol1CntPatientOfLocality;
	private @Value("${sql.hol1.cnt.patient.update.adress4}")	String sqlHol1CntPatientUpdateAdress4;

	@PostMapping("/v/replacePatientsAdress")
	public  @ResponseBody Map<String, Object> replacePatientsAdress(
			@RequestBody Map<String, List> adress12
			, Principal userPrincipal) {
		logger.info("\n  " + "/v/replacePatientsAdress" + adress12);
		List adress1 = adress12.get("adress1");
		List adress2 = adress12.get("adress2");
		Map<String, Object> map = new HashMap<>();
		if(adress1.size()==adress2.size()){
			if(adress1.size()>=1){
				map.put("country1Id", adress1.get(0));
				map.put("country2Id", adress2.get(0));
			}
			if(adress1.size()>=2){
				map.put("district1Id", adress1.get(1));
				map.put("district2Id", adress2.get(1));
			}
			if(adress1.size()>=3){
				map.put("region1Id", adress1.get(2));
				map.put("region2Id", adress2.get(2));
			}
			if(adress1.size()>=4){
				map.put("locality1Id", adress1.get(3));
				map.put("locality2Id", adress2.get(3));
			}
			logger.info("\n  " + map);
			if(adress1.size()>=4){
				hol1EihParamJdbcTemplate.update(sqlHol1CntPatientUpdateAdress4, map);
			}
			
		}
		return map;
	}
	@PostMapping("/v/cntPatientsOfAdress")
	public  @ResponseBody Map<String, Object> saveMoveTodayPatients(
			@RequestBody List<Integer> adress
			, Principal userPrincipal) {
		logger.info("\n Start " + "/cntPatientsOfAdress" + adress);
		Map<String, Object> map = new HashMap<>();
		logger.info("\n adress.size()" +  +adress.size());
		if(adress.size()>=1){
			map.put("countryId", adress.get(0));
			Map<String, Object> cntCountry = hol1EihParamJdbcTemplate.queryForMap(sqlHol1CntPatientOfCountry, map);
			map.put("cntCountry", cntCountry);
		}
		if(adress.size()>=2){
			map.put("districtId", adress.get(1));
			Map<String, Object> cntDistrict = hol1EihParamJdbcTemplate.queryForMap(sqlHol1CntPatientOfDistrict, map);
			map.put("cntDistrict", cntDistrict);
		}
		if(adress.size()>=3){
			map.put("regionId", adress.get(2));
			Map<String, Object> cntRegion = hol1EihParamJdbcTemplate.queryForMap(sqlHol1CntPatientOfRegion, map);
			map.put("cntRegion", cntRegion);
		}
		if(adress.size()>=4){
			map.put("localityId", adress.get(3));
			Map<String, Object> cntLocality = hol1EihParamJdbcTemplate.queryForMap(sqlHol1CntPatientOfLocality, map);
			map.put("cntLocality", cntLocality);
		}
		return map;
	}
	
	private @Value("${sql.hol1.locality.of.region}") String sqlHol1LocalityOfRegion;

	@GetMapping("/v/localityOfRegion-{regionId}")
	public @ResponseBody List<Map<String,Object>> localityOfRegion(@PathVariable Integer regionId) {
		logger.info("\n ------------------------- "+"/v/localityOfRegion-{regionId}" + regionId);
		Map<String, Object> map = new HashMap<>();
		map.put("regionId", regionId);
		List<Map<String, Object>> localityOfRegion
		= hol1EihParamJdbcTemplate.queryForList(sqlHol1LocalityOfRegion,map);
		return localityOfRegion;
	}

	private @Value("${sql.hol1.region.of.district}") String sqlHol1RegionOfDistrict;
	@GetMapping("/v/regionOfDistrict-{districtId}")
	public @ResponseBody List<Map<String,Object>> regionOfDistrict(@PathVariable Integer districtId) {
		logger.info("\n ------------------------- "+"/v/districtOfCountry-{countryId}" + districtId);
		Map<String, Object> map = new HashMap<>();
		map.put("districtId", districtId);
		List<Map<String, Object>> regionOfDistrict
		= hol1EihParamJdbcTemplate.queryForList(sqlHol1RegionOfDistrict,map);
		return regionOfDistrict;
	}

	private @Value("${sql.hol1.district.of.country}") String sqlHol1DistrictOfCountries;

	@GetMapping("/v/districtOfCountry-{countryId}")
	public @ResponseBody List<Map<String,Object>> districtOfCountries(@PathVariable Integer countryId) {
		logger.info("\n ------------------------- "+"/v/districtOfCountry-{countryId}" + countryId);
		Map<String, Object> map = new HashMap<>();
		map.put("countryId", countryId);
		List<Map<String, Object>> districtOfCountry
		= hol1EihParamJdbcTemplate.queryForList(sqlHol1DistrictOfCountries,map);
		return districtOfCountry;
	}

	private @Value("${sql.hol1.countries}") String sqlHol1Countries;

	@GetMapping("/v/countries")
	public @ResponseBody List<Map<String,Object>> countries() {
		logger.info("\n ------------------------- "+"/v/countries");
		List<Map<String, Object>> countries
		= hol1EihJdbcTemplate.queryForList(sqlHol1Countries);
		return countries;
	}

}
