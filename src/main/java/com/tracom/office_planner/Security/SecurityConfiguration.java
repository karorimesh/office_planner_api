package com.tracom.office_planner.Security;

import com.tracom.office_planner.User.CustomUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Bean
    public UserDetailsService userDetailsService(){
        return new CustomUserService();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider  = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
//                .authorizeRequests().anyRequest().permitAll();
                .authorizeRequests()
                .antMatchers("/delete_user/**",
                        "/edit_user/**","/add_user",
                        "/new_board","/delete_board/**",
                        "/edit/**", "/delete_meet/**",
                        "/reschedule/**")
                .hasAuthority("admin")
                .antMatchers("/list_users","/new_meet",
                        "/meeting","/home",
                        "/boardroom","/my_meeting",
                        "/boardroom/page/**","meeting/page/**",
                        "/my_meeting/page/**","/list_users/page/**",
                        "/calendar","/my_profile")
                .authenticated()
                .anyRequest()
                .permitAll()
                .and()
                .formLogin()
                .loginPage("/login")
                .usernameParameter("username")
                .defaultSuccessUrl("/home")
                .permitAll()
                .and()
                .logout()
                .logoutSuccessUrl("/landing")
                .permitAll()
                .and()
                .exceptionHandling()
                .accessDeniedPage("/403")
                ;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/css/**");
        web.ignoring().antMatchers("/js/**");
        web.ignoring().antMatchers("/images/**");
    }
}
