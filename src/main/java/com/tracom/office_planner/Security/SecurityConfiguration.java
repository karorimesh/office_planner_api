package com.tracom.office_planner.Security;

import com.tracom.office_planner.MeetingsLog.PlannerLogger;
import com.tracom.office_planner.User.CustomUserService;
import com.tracom.office_planner.User.User;
import com.tracom.office_planner.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private LoginFailure loginFailure;
    @Autowired
    private LoginSuccess loginSuccess;
    @Autowired
    private UserRepository userRepo;



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
                        "/meeting/**","/home",
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
                .failureHandler(loginFailure)
                .successHandler(loginSuccess)
                .defaultSuccessUrl("/home")
                .permitAll()
                .and()
                .logout()
//                .logoutSuccessHandler((request, response, authentication) -> {
//                    Principal principal = request.getUserPrincipal();
//                    String name = principal.getName();
//                    User user = userRepo.findUserByName(name);
//                    PlannerLogger.loggedOutUser(user);
//                })
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
