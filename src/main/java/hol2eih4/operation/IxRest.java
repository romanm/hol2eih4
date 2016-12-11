package hol2eih4.operation;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IxRest extends IxBasicRest{
	private static final Logger logger = LoggerFactory.getLogger(IxRest.class);

	@RequestMapping(value = "/v/readAuthorityUser", method = RequestMethod.GET)
	public  @ResponseBody Map<String, Object> readAuthorityUser(Principal principal, HttpServletRequest request) {
		logger.info("\n ------------------------- Start /readHome");
		Map<String, Object> model = new HashMap<String, Object>();
		DateTime today = new DateTime();
		model.put("today", today.toDate());
		model.put("principal", principal);
		System.out.println(principal);
		if(principal != null)
			useAuthorityRole(principal, model);
		return model;
	}

//	@RequestMapping("/v/eix")
	@RequestMapping("/eix")
	public String ixStaart(@RequestParam(value="name", required=false, defaultValue="World") String name, Model model) {
		logger.info(" \n -----------------------  Start /v/eix/"+name);
		initModel(model);
		model.addAttribute("name", name);
		System.out.println(model);
		return "ix";
	}
//	@RequestMapping("/v/eix/{id}")
	@RequestMapping("/eix/{id}")
	public String ixWithId(@PathVariable Integer id, Model model) {
		logger.info("\n ------------------------- Start /eix/"+id);
		initModel(model);
		model.addAttribute("ix", id);
		System.out.println(model);
		return "ix";
	}
	@RequestMapping("/")
	public String home(Model model) {
		logger.info("\n ------------------------- Start /");
		initModel(model);
		System.out.println(model);
		return "home";
	}

	private String addQueryString(HttpServletRequest request, String redirectUrl) {
		if(request.getQueryString()!=null)
			if(request.getQueryString().length()>0)
				redirectUrl += "?"+request.getQueryString();
		System.out.println(redirectUrl);
		return redirectUrl;
	}
	
	@RequestMapping(value = "/throughlogin/gotopage/{page1}/{page2}/{page3}", method = RequestMethod.GET)
	public String throughLoginGoToPage(
			@PathVariable String page1,
			@PathVariable String page2,
			@PathVariable String page3,
			HttpServletRequest request ) {
		logger.debug("throughLoginGoToPage -- "+page1+"/"+page2+"/"+page3);
		System.out.println("throughLoginGoToPage -- "+page1+"/"+page2+"/"+page3);
		String redirectUrl = "redirect:/"+page1+"/"+page2+"/"+page3;
		redirectUrl = addQueryString(request, redirectUrl);
		return redirectUrl;
	}
	@RequestMapping(value = "/throughlogin/gotopage/{page1}/{page2}", method = RequestMethod.GET)
	public String throughLoginGoToPage(
			@PathVariable String page1,
			@PathVariable String page2,
			HttpServletRequest request ) {
		logger.debug("throughLoginGoToPage -- "+page1+"/"+page2);
		System.out.println("throughLoginGoToPage -- "+page1+"/"+page2);
		String redirectUrl = "redirect:/"+page1+"/"+page2;
		if("h".equals(page1))
			redirectUrl += ".html";
		redirectUrl = addQueryString(request, redirectUrl);
		return redirectUrl;
	}
	@RequestMapping(value = "/throughlogin/gotopage/{page}", method = RequestMethod.GET)
	public String throughLoginGoToPage(
			@PathVariable String page, HttpServletRequest request ) {
		logger.debug(""+page);
		System.out.println("throughLoginGoToPage -- "+page);
		String redirectUrl = "redirect:/"+page;
		System.out.println(redirectUrl);
		return redirectUrl;
	}
	
}
