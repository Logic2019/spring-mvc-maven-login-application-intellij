package com.lynas.config;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;

//import org.springframework.context.annotation.Configuration;
//
//@Configuration
@EnableWebSecurity
public class RootConfig extends WebSecurityConfigurerAdapter{
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws  Exception {
        auth.inMemoryAuthentication().withUser("admin").password("admin123").roles("USER");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().hasAnyRole("USER","ADMIN")
                .and()
                .authorizeRequests().antMatchers("/login**").permitAll()
                .and()
                .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/signin")
                .usernameParameter("userid")
                .passwordParameter("passwd")
                .successHandler((req,res,auth)->{
                    for (GrantedAuthority authority : auth.getAuthorities()) {
                        System.out.println(auth.getName());
                    }
                    System.out.println(auth.getName());
                    res.sendRedirect("/");
                })
                .failureHandler((req,res,exp)->{
                    String errMsg="";
                    if(exp.getClass().isAssignableFrom(BadCredentialsException.class)){
                        errMsg="Invalid username or password.";
                    }else{
                        errMsg="Unknown error -"+exp.getMessage();
                    }
                    req.getSession().setAttribute("message", errMsg);
                    res.sendRedirect("/login");
                })
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/signout")
                .logoutSuccessHandler((req,res,auth)->{
                    req.getSession().setAttribute("message", "You are logged out successfully.");
                    res.sendRedirect("/login");
                })
                .permitAll()
                .and()
                .csrf().disable();
    }
}
