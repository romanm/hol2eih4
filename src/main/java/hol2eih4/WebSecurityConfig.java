package hol2eih4;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
		.csrf().disable()
		.authorizeRequests()
		.antMatchers("/", "/home"
				, "/css/**"
				, "/js/**"
				, "/r/**"
				, "/v/**" // REST group with free access
				, "/h/**" // html files collection
				, "/img/**" 
				//                		, "/create-read-2015-07-21-excel" 
				//                		, "/readMove-2015-07-21-Patients" 
				//                		, "/mvPatientInWeekDay.html"
				).permitAll()
		.anyRequest().authenticated()
		.and()
		.formLogin()
		.loginPage("/login")
		.permitAll()
		.and()
		.logout()
		.permitAll();
	}
	@Autowired private AppService appService;
	
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		appService.setAuthentication(auth);
		auth
		.inMemoryAuthentication()
		.withUser("рух").password("рух").roles("ruh");
		auth
		.inMemoryAuthentication()
		.withUser("ruh").password("ruh").roles("ruh");
//		auth
//		.inMemoryAuthentication()
//		.withUser("user").password("password").roles("USER");
	}
}