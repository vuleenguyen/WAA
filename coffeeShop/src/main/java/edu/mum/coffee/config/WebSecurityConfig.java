package edu.mum.coffee.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/", "/home", "/index", "/resources/css/*", "/resources/image/*","/resources/js/*").permitAll()
                .antMatchers("/product/action**","/product/add","/product/list","/product/modify", 
                		"/person/list", "/person/add", "/order/list", "/order/detail")
                .hasRole("ADMIN")
                .antMatchers("/person/changeInformation/**", "/product/addToCart**", "/order", "/order/checkout").hasRole("USER")
                .anyRequest().authenticated()
                .and()
            .formLogin()
            	.loginPage("/login")
            	.defaultSuccessUrl("/home")
            	.failureUrl("/login?error")
            	.usernameParameter("username")
            	.passwordParameter("password")
            	.permitAll()
            	.and()
            .logout()
            	.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
            	.logoutSuccessUrl("/home")
            	.invalidateHttpSession(true)
            	.permitAll()
            .and().exceptionHandling().accessDeniedPage("/403")
            	;
    }

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication().withUser("admin").password("admin").roles("ADMIN");
		auth.inMemoryAuthentication().withUser("vuleenguyen.92@gmail.com").password("123456").roles("USER");
	}
}