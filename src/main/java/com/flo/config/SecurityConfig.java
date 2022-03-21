package com.flo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Component
@Configuration
@ComponentScan(basePackages = { "com.flo.config" })
public class SecurityConfig extends WebSecurityConfigurerAdapter
{
	@Value("${spring.application.name:'flo-test-user'}")
	private String appName;

	@Bean
	public PasswordEncoder encoder()
	{
		return new BCryptPasswordEncoder(11);
	}
	
	@Autowired	private UserDetailsServiceImpl userDetailsService;

	@Bean
	public DaoAuthenticationProvider authProvider()
	{
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(encoder());
		return authProvider;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception
	{
		http.httpBasic().
	        realmName(appName).
	        and()
	        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
	        .and()
	        .csrf().disable()
			.cors()
			.and()
			.authorizeRequests()
				.antMatchers("/actuator/**").permitAll()
			.anyRequest().authenticated()
			.and()
			.httpBasic();
	}
	
    // Used by spring security if CORS is enabled.
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

	@Override
	public void configure(WebSecurity web) throws Exception
	{
		web.ignoring().antMatchers("/*.css");
		web.ignoring().antMatchers("/*.js");
	}
}
